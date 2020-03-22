package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_FINAL
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_PRIVATE
import org.jetbrains.org.objectweb.asm.Opcodes.ACC_STATIC
import org.jetbrains.org.objectweb.asm.Type

class HoistRegexExpressionCodegenInterceptor(private val patternAllocator: PatternAllocator) :
    ExpressionCodegenExtension {
    override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {

        val patterns = patternAllocator.allocatedForClass(codegen.className).takeIf { it.isNotEmpty() } ?: return

        val clinitCodegen = codegen.createOrGetClInitCodegen()
        val regexType = Type.getObjectType("kotlin/text/Regex")

        for ((className, symbol, pattern) in patterns) {
            codegen.v.newField(
                JvmDeclarationOrigin.NO_ORIGIN, ACC_STATIC or ACC_PRIVATE or ACC_FINAL,
                symbol, regexType.descriptor, null, null
            )
            clinitCodegen.v.anew(regexType)
            clinitCodegen.v.dup()
            clinitCodegen.v.aconst(pattern)
            clinitCodegen.v.invokespecial("kotlin/text/Regex", "<init>", "(Ljava/lang/String;)V", false)
            clinitCodegen.v.putstatic(className, symbol, regexType.descriptor)
        }
    }
}
