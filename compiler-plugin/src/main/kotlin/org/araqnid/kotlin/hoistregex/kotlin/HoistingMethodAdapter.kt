package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.Attribute
import org.jetbrains.org.objectweb.asm.Handle
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.TypePath
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import java.util.EnumSet

// Want to convert a sequence like this:
//       11: new           #19                 // class kotlin/text/Regex
//      14: dup
//      15: ldc           #21                 // String \\S+
//      17: invokespecial #25                 // Method kotlin/text/Regex."<init>":(Ljava/lang/String;)V
// into:
//    new   // class kotlin/text/Regex
//    getstatic // Field $regex$whatever on theClass
//    invokespecial

class HoistingMethodAdapter(
    private val className: String,
    private val patternAllocator: PatternAllocator,
    private val original: MethodVisitor
) : MethodVisitor(Opcodes.ASM5, original) {
    private var currentExpectations: Expectations<PatternAllocator.Pattern>? = null

    companion object {
        private const val dup = "visitInsn:${Opcodes.DUP}"
        private const val aastore = "visitInsn:${Opcodes.AASTORE}"
        private const val ldcPrefix = "visitLdcInsn:"
        private const val constructor = "visitMethodInsn:${Opcodes.INVOKESPECIAL}:kotlin/text/Regex:<init>:(Ljava/lang/String;)V:false"
        private const val constructorSingleOption = "visitMethodInsn:${Opcodes.INVOKESPECIAL}:kotlin/text/Regex:<init>:(Ljava/lang/String;Lkotlin/text/RegexOption;)V:false"
        private const val constructorMultipleOptions = "visitMethodInsn:${Opcodes.INVOKESPECIAL}:kotlin/text/Regex:<init>:(Ljava/lang/String;Ljava/util/Set;)V:false"
        private val getOptionPattern = Regex("visitFieldInsn:${Opcodes.GETSTATIC}:kotlin/text/RegexOption:([A-Z_]+):Lkotlin/text/RegexOption;")
        private const val createRegexOptionArray = "visitTypeInsn:${Opcodes.ANEWARRAY}:kotlin/text/RegexOption"
        private const val invokeSetOf = "visitMethodInsn:${Opcodes.INVOKESTATIC}:kotlin/collections/SetsKt:setOf:([Ljava/lang/Object;)Ljava/util/Set;:false"

        suspend fun MatcherScope<String>.takeInsn(): String {
            while (true) {
                val insn = take()
                if (!insn.startsWith("visitLabel:") && !insn.startsWith("visitLineNumber:")) {
                    return insn
                }
            }
        }

        suspend fun MatcherScope<String>.matchRegexInstructions(): PatternAllocator.Pattern? {
            if (takeInsn() != dup) return null
            val patternInsn = takeInsn()
            if (!patternInsn.startsWith(ldcPrefix)) return null
            val pattern = patternInsn.substring(ldcPrefix.length)
            val optionsOrConstructor = takeInsn()
            if (optionsOrConstructor == constructor) return PatternAllocator.Pattern(pattern, emptySet())
            val option = decodeGetOption(optionsOrConstructor)
            if (option != null) {
                // single option
                if (takeInsn() != constructorSingleOption) return null
                return PatternAllocator.Pattern(pattern, setOf(option))
            }
            val options = matchMultipleOptions(optionsOrConstructor)
            if (options != null) {
                if (takeInsn() != constructorMultipleOptions) return null
                return PatternAllocator.Pattern(pattern, options)
            }
            return null
        }

        private fun decodeGetOption(insn: String): RegexOption? {
            val getOptionMatch = getOptionPattern.matchEntire(insn) ?: return null
            val option = getOptionMatch.groupValues[1]
            return RegexOption.valueOf(option)
        }

        // multiple options? can't handle in general, but one common construction is:
        //   Regex("....", setOf(RegexOption.A, RegexOption.B))
        //  which is emitted as
        //   ICONST_2 etc (number of choices)
        //   ANEWARRAY kotlin/text/RegexOption
        //   DUP
        //   ICONST_0
        //   GETSTATIC kotlin/text/RegexOption.IGNORE_CASE : Lkotlin/text/RegexOption;
        //   AASTORE
        //   DUP
        //   ICONST_1
        //   GETSTATIC kotlin/text/RegexOption.MULTILINE : Lkotlin/text/RegexOption;
        //   AASTORE
        //   INVOKESTATIC kotlin/collections/SetsKt.setOf ([Ljava/lang/Object;)Ljava/util/Set;
        //   INVOKESPECIAL kotlin/text/Regex.<init> (Ljava/lang/String;Ljava/util/Set;)V
        private suspend fun MatcherScope<String>.matchMultipleOptions(possibleSizeInsn: String): Set<RegexOption>? {
            decodeIntConstant(possibleSizeInsn) ?: return null
            if (takeInsn() != createRegexOptionArray) return null
            val options = EnumSet.noneOf(RegexOption::class.java)
            if (takeInsn() != dup) return null
            while (true) {
                decodeIntConstant(takeInsn()) ?: return null
                val option = decodeGetOption(takeInsn()) ?: return null
                if (takeInsn() != aastore) return null
                options += option
                when (takeInsn()) {
                    dup -> Unit
                    invokeSetOf -> return options
                    else -> return null
                }
            }
        }

        private fun decodeIntConstant(insn: String): Int? {
            return when (insn) {
                "visitInsn:${Opcodes.ICONST_0}" -> 0 // Whyyyy
                "visitInsn:${Opcodes.ICONST_1}" -> 1
                "visitInsn:${Opcodes.ICONST_2}" -> 2
                "visitInsn:${Opcodes.ICONST_3}" -> 3
                "visitInsn:${Opcodes.ICONST_4}" -> 4
                "visitInsn:${Opcodes.ICONST_5}" -> 5
                else -> null
            }
        }
    }

    override fun visitTypeInsn(opcode: Int, type: String) {
        if (shiftExpectations("visitTypeInsn:$opcode:$type") { visitTypeInsn(opcode, type) })
            return
        when (opcode) {
            Opcodes.NEW -> {
                if (type == "kotlin/text/Regex") {
                    val reapply: MethodVisitor.() -> Unit = { visitTypeInsn(opcode, type) }
                    currentExpectations = Expectations(
                        node = matchSequence { matchRegexInstructions() },
                        onMismatch = listOf(reapply),
                        onMet = { pattern ->
                            InstructionAdapter(this).apply {
                                val allocated = patternAllocator.allocate(className, pattern)
                                getstatic(className, allocated.symbol, "Lkotlin/text/Regex;")
                            }
                        })
                    return
                }
            }
        }
        super.visitTypeInsn(opcode, type)
    }

    override fun visitInsn(opcode: Int) {
        if (shiftExpectations("visitInsn:$opcode") { visitInsn(opcode) })
            return
        super.visitInsn(opcode)
    }

    override fun visitLdcInsn(value: Any?) {
        if (shiftExpectations("visitLdcInsn:$value") { visitLdcInsn(value) })
            return
        super.visitLdcInsn(value)
    }

    override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        throw UnsupportedOperationException()
    }

    override fun visitMethodInsn(opcode: Int, owner: String, name: String, descriptor: String, isInterface: Boolean) {
        if (shiftExpectations("visitMethodInsn:$opcode:$owner:$name:$descriptor:$isInterface")
            { visitMethodInsn(opcode, owner, name, descriptor, isInterface) }
        )
            return
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }

    override fun visitMultiANewArrayInsn(descriptor: String?, numDimensions: Int) {
        failedExpectation()
        super.visitMultiANewArrayInsn(descriptor, numDimensions)
    }

    override fun visitFrame(type: Int, numLocal: Int, local: Array<out Any>?, numStack: Int, stack: Array<out Any>?) {
        failedExpectation()
        super.visitFrame(type, numLocal, local, numStack, stack)
    }

    override fun visitVarInsn(opcode: Int, `var`: Int) {
        failedExpectation()
        super.visitVarInsn(opcode, `var`)
    }

    override fun visitTryCatchBlock(start: Label?, end: Label?, handler: Label?, type: String?) {
        failedExpectation()
        super.visitTryCatchBlock(start, end, handler, type)
    }

    override fun visitLookupSwitchInsn(dflt: Label?, keys: IntArray?, labels: Array<out Label>?) {
        failedExpectation()
        super.visitLookupSwitchInsn(dflt, keys, labels)
    }

    override fun visitJumpInsn(opcode: Int, label: Label?) {
        failedExpectation()
        super.visitJumpInsn(opcode, label)
    }

    override fun visitAnnotableParameterCount(parameterCount: Int, visible: Boolean) {
        failedExpectation()
        super.visitAnnotableParameterCount(parameterCount, visible)
    }

    override fun visitIntInsn(opcode: Int, operand: Int) {
        failedExpectation()
        super.visitIntInsn(opcode, operand)
    }

    override fun visitAnnotationDefault(): AnnotationVisitor {
        failedExpectation()
        return super.visitAnnotationDefault()
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        failedExpectation()
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitTypeAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        failedExpectation()
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible)
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        failedExpectation()
        super.visitMaxs(maxStack, maxLocals)
    }

    override fun visitInvokeDynamicInsn(
        name: String?,
        descriptor: String?,
        bootstrapMethodHandle: Handle?,
        vararg bootstrapMethodArguments: Any?
    ) {
        failedExpectation()
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, *bootstrapMethodArguments)
    }

    override fun visitLabel(label: Label) {
        if (shiftExpectations("visitLabel:$label") { visitLabel(label) })
            return
        super.visitLabel(label)
    }

    override fun visitTryCatchAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        failedExpectation()
        return super.visitTryCatchAnnotation(typeRef, typePath, descriptor, visible)
    }

    override fun visitInsnAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        failedExpectation()
        return super.visitInsnAnnotation(typeRef, typePath, descriptor, visible)
    }

    override fun visitParameterAnnotation(parameter: Int, descriptor: String?, visible: Boolean): AnnotationVisitor {
        failedExpectation()
        return super.visitParameterAnnotation(parameter, descriptor, visible)
    }

    override fun visitIincInsn(`var`: Int, increment: Int) {
        failedExpectation()
        super.visitIincInsn(`var`, increment)
    }

    override fun visitLineNumber(line: Int, start: Label) {
        if (shiftExpectations("visitLineNumber:$line:$start") { visitLineNumber(line, start) })
            return
        super.visitLineNumber(line, start)
    }

    override fun visitLocalVariableAnnotation(
        typeRef: Int,
        typePath: TypePath?,
        start: Array<out Label>?,
        end: Array<out Label>?,
        index: IntArray?,
        descriptor: String?,
        visible: Boolean
    ): AnnotationVisitor {
        failedExpectation()
        return super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, descriptor, visible)
    }

    override fun visitTableSwitchInsn(min: Int, max: Int, dflt: Label?, vararg labels: Label?) {
        failedExpectation()
        super.visitTableSwitchInsn(min, max, dflt, *labels)
    }

    override fun visitEnd() {
        failedExpectation()
        super.visitEnd()
    }

    override fun visitLocalVariable(
        name: String?,
        descriptor: String?,
        signature: String?,
        start: Label?,
        end: Label?,
        index: Int
    ) {
        failedExpectation()
        super.visitLocalVariable(name, descriptor, signature, start, end, index)
    }

    override fun visitParameter(name: String?, access: Int) {
        failedExpectation()
        super.visitParameter(name, access)
    }

    override fun visitAttribute(attribute: Attribute?) {
        failedExpectation()
        super.visitAttribute(attribute)
    }

    override fun visitFieldInsn(opcode: Int, owner: String, name: String, descriptor: String) {
        if (shiftExpectations("visitFieldInsn:$opcode:$owner:$name:$descriptor")
            { visitFieldInsn(opcode, owner, name, descriptor) })
            return
        super.visitFieldInsn(opcode, owner, name, descriptor)
    }

    override fun visitCode() {
        failedExpectation()
        super.visitCode()
    }

    private fun shiftExpectations(reality: String, op: MethodVisitor.() -> Unit): Boolean {
        val expectations = currentExpectations ?: return false
        return when (val matchResult = expectations.node(reality)) {
            is SequenceMatchResult.Mismatch -> {
                // not matched
                failedExpectation()
                false // not matched - don't eat instruction
            }
            is SequenceMatchResult.Matched -> {
                currentExpectations = null
                expectations.onMet(matchResult.value)
                true // matched - eat instruction
            }
            null -> {
                currentExpectations = expectations.copy(
                    onMismatch = expectations.onMismatch + op
                )
                true // matched - eat instruction
            }
        }
    }

    private fun failedExpectation() {
        val expectations = currentExpectations ?: return
        for (flusher in expectations.onMismatch) {
            original.flusher()
        }
        currentExpectations = null
    }

    private data class Expectations<T : Any>(
        val node: SequenceMatcher<String, T>,
        val onMismatch: List<MethodVisitor.() -> Unit>,
        val onMet: (T) -> Unit
    )
}
