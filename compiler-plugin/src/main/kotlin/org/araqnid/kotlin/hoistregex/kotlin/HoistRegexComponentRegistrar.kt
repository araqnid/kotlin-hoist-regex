package org.araqnid.kotlin.hoistregex.kotlin

import com.google.auto.service.AutoService
import org.araqnid.kotlin.hoistregex.kotlin.ConfigurationKeys.KEY_ENABLED
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(ComponentRegistrar::class)
class HoistRegexComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false)
            return

        ClassBuilderInterceptorExtension.registerExtension(
            project,
            HoistRegexClassGenerationInterceptor(
            )
        )

        ExpressionCodegenExtension.registerExtension(
            project,
            HoistRegexExpressionCodegenInterceptor(
            )
        )
    }
}
