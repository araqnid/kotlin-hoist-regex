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
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class HoistRegexTest {
    @get:Rule
    val compilerEnvironment = CompilerEnvironmentRule()

    @Test
    fun `test code compiled with plugin is manipulated`() {
        KotlinToJVMBytecodeCompiler.compileBunchOfSources(compilerEnvironment.environment)
        val process = Runtime.getRuntime().exec(
            arrayOf(
                "javap",
                "-c",
                "-classpath",
                compilerEnvironment.tempDirectory.toString(),
                "testInput.Example"
            )
        )
        process.inputStream.bufferedReader().lineSequence().forEach { line ->
            println("javap: $line")
        }
        process.waitFor()
    }

    class CompilerEnvironmentRule : TestRule {
        private lateinit var description: Description
        lateinit var tempDirectory: File
        private val testMethodName by lazy { description.methodName!! }
        private val testClassName by lazy { description.className!! }
        private val testName by lazy { "$testClassName.$testMethodName" }

        override fun apply(base: Statement, description: Description): Statement {
            return object : Statement() {
                override fun evaluate() {
                    this@CompilerEnvironmentRule.description = description
                    createTempDirectory()
                    try {
                        base.evaluate()
                    } finally {
                        deleteTempDirectory()
                    }
                }
            }
        }

        private fun createTempDirectory() {
            tempDirectory = Files.createTempDirectory("kotlin-hoist-regex-test").toFile()
        }

        private fun deleteTempDirectory() {
            deleteRecursively(tempDirectory.toPath())
        }

        private fun deleteRecursively(path: Path) {
            for (member in Files.list(path).use { it.collect(Collectors.toList()) }) {
                when {
                    Files.isSymbolicLink(member) || Files.isRegularFile(member) -> Files.delete(member)
                    Files.isDirectory(member) -> deleteRecursively(member)
                    else -> error("Unknown type of file: $member")
                }
            }
            Files.delete(path)
        }

        val configuration by lazy {
            CompilerConfiguration().apply {
                put(CommonConfigurationKeys.MODULE_NAME, "test-module")
                put(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, TestingMessageCollector)
                put(JVMConfigurationKeys.OUTPUT_DIRECTORY, tempDirectory)

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