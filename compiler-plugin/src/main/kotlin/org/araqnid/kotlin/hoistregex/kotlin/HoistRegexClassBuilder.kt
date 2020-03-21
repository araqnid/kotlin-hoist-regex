package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Opcodes.NEW
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

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
        return object : MethodVisitor(Opcodes.ASM5, original) {
            override fun visitTypeInsn(opcode: Int, type: String?) {
                when (opcode) {
                    NEW -> {
                        InstructionAdapter(this).apply {
                            if (type == "kotlin/text/Regex") {
                                println("seen Regex creation: origin.descriptor=${origin.descriptor} access=$access method=$name type=$type")
                            }
                        }
                    }
                }
                super.visitTypeInsn(opcode, type)
            }
        }
    }
}
