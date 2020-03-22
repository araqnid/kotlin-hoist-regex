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
        val regexOptionType = Type.getObjectType("kotlin/text/RegexOption")

        for ((className, symbol, pattern) in patterns) {
            codegen.v.newField(
                JvmDeclarationOrigin.NO_ORIGIN, ACC_STATIC or ACC_PRIVATE or ACC_FINAL,
                symbol, regexType.descriptor, null, null
            )
            clinitCodegen.v.anew(regexType)
            clinitCodegen.v.dup()
            clinitCodegen.v.aconst(pattern.source)
            when (pattern.options.size) {
                0 -> {
                    clinitCodegen.v.invokespecial("kotlin/text/Regex", "<init>", "(Ljava/lang/String;)V", false)
                }
                1 -> {
                    clinitCodegen.v.getstatic("kotlin/text/RegexOption", pattern.options.first().name, "Lkotlin/text/RegexOption;")
                    clinitCodegen.v.invokespecial("kotlin/text/Regex", "<init>", "(Ljava/lang/String;Lkotlin/text/RegexOption;)V", false)
                }
                else -> {
                    clinitCodegen.v.iconst(pattern.options.size)
                    clinitCodegen.v.newarray(regexOptionType)
                    for ((index, option) in pattern.options.withIndex()) {
                        clinitCodegen.v.dup()
                        clinitCodegen.v.iconst(index)
                        clinitCodegen.v.getstatic("kotlin/text/RegexOption", option.name, "Lkotlin/text/RegexOption;")
                        clinitCodegen.v.astore(regexOptionType)
                    }
                    clinitCodegen.v.invokestatic("kotlin/collections/SetsKt", "setOf","([Ljava/lang/Object;)Ljava/util/Set;", false)
                    clinitCodegen.v.invokespecial("kotlin/text/Regex", "<init>", "(Ljava/lang/String;Ljava/util/Set;)V", false)
                }
            }
            clinitCodegen.v.putstatic(className, symbol, regexType.descriptor)
        }
    }
}
