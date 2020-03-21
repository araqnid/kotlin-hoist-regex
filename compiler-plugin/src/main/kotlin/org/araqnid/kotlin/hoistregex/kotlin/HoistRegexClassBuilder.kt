package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Opcodes.NEW

// Want to convert a sequence like this:
//       11: new           #19                 // class kotlin/text/Regex
//      14: dup
//      15: ldc           #21                 // String \\S+
//      17: invokespecial #25                 // Method kotlin/text/Regex."<init>":(Ljava/lang/String;)V
// into:
//    new   // class kotlin/text/Regex
//    getstatic // Field $regex$whatever on theClass
//    invokespecial

class HoistRegexClassBuilder(private val delegateBuilder: ClassBuilder) : DelegatingClassBuilder() {
    override fun getDelegate(): ClassBuilder = delegateBuilder

    override fun newMethod(
        origin: JvmDeclarationOrigin,
        access: Int,
        name: String,
        desc: String,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val original = super.newMethod(origin, access, name, desc, signature, exceptions)
        if (name == "<clinit>")
            return original
        return HoistingMethodAdapter(original)
    }
}

class HoistingMethodAdapter(private val original: MethodVisitor) : MethodVisitor(Opcodes.ASM5, original) {
    private var expectations: List<Expectation>? = null
    private var onExpectationMismatch: MutableList<(() -> Unit)>? = null

    override fun visitTypeInsn(opcode: Int, type: String) {
        when (opcode) {
            NEW -> {
                if (type == "kotlin/text/Regex") {
                    expectations = listOf(
                        Expectation("visitInsn:" + Opcodes.DUP),
                        Expectation("visitInsn:" + Opcodes.LDC),
                        Expectation("visitInvokeSpecial:" + Opcodes.INVOKESPECIAL + ":<init>")
                    )
                    onExpectationMismatch = mutableListOf()
                    onExpectationMismatch!!.add {
                        original.visitTypeInsn(opcode, type)
                    }
                }
            }
        }
        super.visitTypeInsn(opcode, type)
    }

    private data class Expectation(val tag: String)
}
