package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Opcodes.ARETURN
import org.jetbrains.org.objectweb.asm.Opcodes.IRETURN
import org.jetbrains.org.objectweb.asm.Opcodes.RETURN
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
        val function = origin.descriptor as? FunctionDescriptor ?: return original
        return object : MethodVisitor(Opcodes.ASM5, original) {
            override fun visitCode() {
                super.visitCode()
                InstructionAdapter(this).apply {
                    println("seen method entry: origin=$origin access=$access name=$name desc=$desc signature=$signature")
                }
            }

            override fun visitInsn(opcode: Int) {
                when (opcode) {
                    RETURN /* void */, ARETURN /* object */, IRETURN /* int */ -> {
                        InstructionAdapter(this).apply {
                            println("seen method exit: origin=$origin access=$access name=$name desc=$desc signature=$signature")
                        }
                    }
                }
                super.visitInsn(opcode)
            }
        }
    }
}
