package org.araqnid.kotlin.hoistregex.kotlin

import com.google.auto.service.AutoService
import org.araqnid.kotlin.hoistregex.kotlin.ConfigurationKeys.KEY_ENABLED
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.js.translate.extensions.JsSyntheticTranslateExtension

@AutoService(ComponentRegistrar::class)
class HoistRegexComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false)
            return

        val patternAllocator = PatternAllocator()

        ClassBuilderInterceptorExtension.registerExtension(
            project,
            HoistRegexClassGenerationInterceptor(patternAllocator)
        )

        ExpressionCodegenExtension.registerExtension(
            project,
            HoistRegexExpressionCodegenInterceptor(patternAllocator)
        )

        JsSyntheticTranslateExtension.registerExtension(
            project,
            HoistRegexJsSyntheticTranslate(patternAllocator)
        )
    }
}
