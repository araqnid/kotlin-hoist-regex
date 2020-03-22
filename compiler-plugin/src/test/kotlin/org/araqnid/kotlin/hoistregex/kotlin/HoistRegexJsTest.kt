package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.common.output.writeAll
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.js.config.JSConfigurationKeys
import org.jetbrains.kotlin.js.config.JsConfig
import org.jetbrains.kotlin.js.facade.K2JSTranslator
import org.jetbrains.kotlin.js.facade.MainCallParameters
import org.jetbrains.kotlin.js.facade.TranslationResult
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test

class HoistRegexJsTest {
    @get:Rule
    val compilerEnvironment = CompilerEnvironmentRule()

    @Test
    fun `test code compiled with plugin is manipulated`() {
        val translator = K2JSTranslator(compilerEnvironment.jsConfig)
        when (val translationResult = translator.translate(JsReporter,
            compilerEnvironment.environment.getSourceFiles(), MainCallParameters.noCall())) {
            is TranslationResult.Fail ->
                fail("Compiler failed: $translationResult")
            is TranslationResult.SuccessNoCode ->
                fail("Compiler succeeded but produced no code")
            is TranslationResult.Success ->
                translationResult.getOutputFiles(compilerEnvironment.tempDirectory.resolve("output.js"), null, null)
                    .writeAll(compilerEnvironment.tempDirectory, TestingMessageCollector, true)
        }
        val jsOutput = compilerEnvironment.tempDirectory.resolve("output.js").readText()
        println(jsOutput)
    }

    class CompilerEnvironmentRule : TemporaryDirectoryRule() {
        val configuration by lazy {
            CompilerConfiguration().apply {
                put(CommonConfigurationKeys.MODULE_NAME, "test-module")
                put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, TestingMessageCollector)
                put(JSConfigurationKeys.OUTPUT_DIR, tempDirectory)

                val classpathString =
                    System.getenv("TEST_INPUT_JS_COMPILE_CLASSPATH")
                        ?: error("TEST_INPUT_JS_COMPILE_CLASSPATH not specified")
                put(JSConfigurationKeys.LIBRARIES, classpathString.split(':'))

                addKotlinSourceRoots(listOf("src/testInput/kotlin"))
                add(ComponentRegistrar.PLUGIN_COMPONENT_REGISTRARS, HoistRegexComponentRegistrar())
                put(ConfigurationKeys.KEY_ENABLED, true)
            }
        }

        val testDisposable by lazy { DisposableImpl() }

        val environment by lazy {
            KotlinCoreEnvironment.createForTests(testDisposable, configuration, EnvironmentConfigFiles.JS_CONFIG_FILES)
        }

        val jsConfig: JsConfig by lazy {
            val config = JsConfig(environment.project, configuration)

            config
        }

        inner class DisposableImpl : Disposable {
            private var disposed = false

            override fun dispose() {
                disposed = true
            }

            override fun toString(): String = testName
        }
    }
}
