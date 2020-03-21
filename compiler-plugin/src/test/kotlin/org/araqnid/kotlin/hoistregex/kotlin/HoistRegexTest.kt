package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoots
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File

class HoistRegexTest {
    @get:Rule
    val compilerEnvironment = CompilerEnvironmentRule()

    @Test
    fun `test code compiled with plugin is manipulated`() {
        KotlinToJVMBytecodeCompiler.compileBunchOfSources(compilerEnvironment.environment)
    }

    class CompilerEnvironmentRule : TestRule {
        private lateinit var description: Description
        private val testMethodName by lazy { description.methodName!! }
        private val testClassName by lazy { description.className!! }
        private val testName by lazy { "$testClassName.$testMethodName" }

        override fun apply(base: Statement, description: Description): Statement {
            return object : Statement() {
                override fun evaluate() {
                    this@CompilerEnvironmentRule.description = description
                    base.evaluate()
                }
            }
        }

        val configuration by lazy {
            CompilerConfiguration().apply {
                put(CommonConfigurationKeys.MODULE_NAME, "test-module")
                put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, TestingMessageCollector)

                val classpathString =
                    System.getenv("TEST_INPUT_COMPILE_CLASSPATH") ?: error("TEST_INPUT_COMPILE_CLASSPATH not specified")
                addJvmClasspathRoots(classpathString.split(':').map { File(it) })
                addKotlinSourceRoots(listOf("src/testInput/kotlin"))
                add(ComponentRegistrar.PLUGIN_COMPONENT_REGISTRARS, HoistRegexComponentRegistrar())
                put(ConfigurationKeys.KEY_ENABLED, true)
            }
        }

        val testDisposable by lazy { DisposableImpl() }

        val environment by lazy {
            KotlinCoreEnvironment.createForTests(testDisposable, configuration, EnvironmentConfigFiles.JVM_CONFIG_FILES)
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
