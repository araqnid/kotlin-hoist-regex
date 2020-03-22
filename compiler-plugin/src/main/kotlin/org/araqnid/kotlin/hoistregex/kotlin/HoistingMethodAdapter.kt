package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.Attribute
import org.jetbrains.org.objectweb.asm.Handle
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.TypePath
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

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
    private var currentExpectations: Expectations? = null

    override fun visitTypeInsn(opcode: Int, type: String) {
        if (shiftExpectations("visitTypeInsn:$opcode:$type") { visitTypeInsn(opcode, type) })
            return
        when (opcode) {
            Opcodes.NEW -> {
                if (type == "kotlin/text/Regex") {
                    val reapply: MethodVisitor.() -> Unit = { visitTypeInsn(opcode, type) }
                    currentExpectations = Expectations(
                        node = Node.Expectation.ExpectDup,
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

    override fun visitLabel(label: Label?) {
        failedExpectation()
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

    override fun visitLineNumber(line: Int, start: Label?) {
        failedExpectation()
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

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        failedExpectation()
        super.visitFieldInsn(opcode, owner, name, descriptor)
    }

    override fun visitCode() {
        failedExpectation()
        super.visitCode()
    }

    private fun shiftExpectations(reality: String, op: MethodVisitor.() -> Unit): Boolean {
        val expectations = currentExpectations ?: return false
        return when (val next = expectations.node.match(reality)) {
            null -> {
                // not matched
                failedExpectation()
                false // not matched - don't eat instruction
            }
            is Node.Terminal -> {
                currentExpectations = null
                expectations.onMet(next.value)
                true // matched - eat instruction
            }
            is Node.Expectation -> {
                currentExpectations = expectations.copy(
                    node = next,
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

    private data class Expectations(val node: Node.Expectation, val onMismatch: List<MethodVisitor.() -> Unit>, val onMet: (String) -> Unit)

    private sealed class Node {
        sealed class Expectation : Node() {
            abstract fun match(input: String): Node?

            object ExpectDup : Expectation() {
                const val expected = "visitInsn:" + Opcodes.DUP
                override fun match(input: String): Node? {
                    return if (input == expected) ExpectPatternConstant else null
                }
            }

            object ExpectPatternConstant : Expectation() {
                const val prefix = "visitLdcInsn:"
                override fun match(input: String): Node? {
                    return if (input.startsWith(prefix)) ExpectConstructorInvocation(input.substring(prefix.length)) else null
                }
            }

            class ExpectConstructorInvocation(val pattern: String) : Expectation() {
                override fun match(input: String): Node? {
                    return if (input == expected) Terminal(pattern) else null
                }

                companion object {
                    const val expected =
                        "visitMethodInsn:" + Opcodes.INVOKESPECIAL + ":kotlin/text/Regex:<init>:(Ljava/lang/String;)V:false"
                }
            }
        }

        data class Terminal(val value: String) : Node()
    }
}
