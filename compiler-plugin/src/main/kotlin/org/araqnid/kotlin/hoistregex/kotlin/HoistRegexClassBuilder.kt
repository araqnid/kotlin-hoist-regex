package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor

class HoistRegexClassBuilder(private val delegateBuilder: ClassBuilder) : DelegatingClassBuilder() {
    override fun getDelegate(): ClassBuilder = delegateBuilder

    private val allocatedPatterns = mutableMapOf<String, String>()

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
        return HoistingMethodAdapter(thisName, allocatedPatterns, original)
    }
}
