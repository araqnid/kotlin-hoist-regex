package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class HoistingMethodAdapter(private val className: String, private val patternAllocator: PatternAllocator, private val original: MethodVisitor) : MethodVisitor(
    Opcodes.ASM5, original
) {
    private var expectationNode: Node.Expectation? = null
    private var onExpectationMismatch: MutableList<(MethodVisitor.() -> Unit)>? = null
    private var onExpectationsMet: ((String) -> Unit)? = null

    override fun visitTypeInsn(opcode: Int, type: String) {
        if (shiftExpectations("visitTypeInsn:$opcode:$type") { visitTypeInsn(opcode, type) })
            return
        when (opcode) {
            Opcodes.NEW -> {
                if (type == "kotlin/text/Regex") {
                    expectationNode = Node.Expectation.ExpectDup
                    onExpectationMismatch = mutableListOf()
                    onExpectationMismatch!!.add { visitTypeInsn(opcode, type) }
                    onExpectationsMet = { pattern ->

                        InstructionAdapter(this).apply {
                            val allocated = patternAllocator.allocate(className, pattern)
                            getstatic(className, allocated.symbol, "Lkotlin/text/Regex;")
                        }
                    }
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

    private fun shiftExpectations(reality: String, op: MethodVisitor.() -> Unit): Boolean {
        val node = expectationNode ?: return false
        return when (val next = node.match(reality)) {
            null -> {
                // not matched
                for (flusher in onExpectationMismatch!!) {
                    original.flusher()
                }
                expectationNode = null
                onExpectationMismatch = null
                onExpectationsMet = null
                false // not matched - don't eat instruction
            }
            is Node.Terminal -> {
                val finisher = onExpectationsMet!!
                expectationNode = null
                onExpectationMismatch = null
                onExpectationsMet = null
                finisher(next.value)
                true // matched - eat instruction
            }
            is Node.Expectation -> {
                expectationNode = next
                onExpectationMismatch!!.add(op)
                true // matched - eat instruction
            }
        }
    }

    sealed class Node {
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
