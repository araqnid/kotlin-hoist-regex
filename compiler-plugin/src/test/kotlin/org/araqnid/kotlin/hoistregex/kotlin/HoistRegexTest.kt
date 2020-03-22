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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HoistRegexTest {
    @get:Rule
    val compilerEnvironment = CompilerEnvironmentRule()

    @Test
    fun `test code compiled with plugin is manipulated`() {
        KotlinToJVMBytecodeCompiler.compileBunchOfSources(compilerEnvironment.environment)
        val (methodInstructions, javapOutput) = summariseMethodInstructions("testInput.Example")
        val someMethodInstructions = methodInstructions["someMethod"]?.joinToString("\n") ?: ""
        val constructorInstructions = methodInstructions["testInput.Example"]?.joinToString("\n") ?: ""
        assertFalse(someMethodInstructions.contains("""
            |new class kotlin/text/Regex
            |dup 
            |ldc String variablePattern
            |invokespecial Method kotlin/text/Regex."<init>":(Ljava/lang/String;)V
        """.trimMargin()),
            "someMethod should not contain Regex(...) creation sequence at variable initialisation" +
                    "\n\nMethod instructions:\n$someMethodInstructions\n\n$javapOutput"
        )
        assertFalse(constructorInstructions.contains("""
            |new class kotlin/text/Regex
            |dup 
            |ldc String propertyPattern
            |invokespecial Method kotlin/text/Regex."<init>":(Ljava/lang/String;)V
        """.trimMargin()),
            "constructor should not contain Regex(...) creation sequence at property initialisation" +
                    "\n\nConstructor instructions:\n$constructorInstructions\n\n$javapOutput"
        )
        assertTrue(someMethodInstructions.contains("""
            |getstatic Field ${'$'}pattern${'$'}0:Lkotlin/text/Regex;
        """.trimMargin()),
            "someMethod should contain cached regex access sequence at variable initialisation" +
                    "\n\nMethod instructions:\n$someMethodInstructions\n\n$javapOutput"
        )
        assertTrue(constructorInstructions.contains("""
            |getstatic Field ${'$'}pattern${'$'}1:Lkotlin/text/Regex;
        """.trimMargin()),
            "constructor should contain cached regex access sequence at property initialisation" +
                    "\n\nConstructor instructions:\n$constructorInstructions\n\n$javapOutput"
        )
        assertTrue(javapOutput.contains("""
            |private static final kotlin.text.Regex ${'$'}pattern${'$'}0
        """.trimMargin()),
            "class should contain static field definition" +
                    "\n\n$javapOutput"
        )
        assertTrue(javapOutput.contains("""
            |private static final kotlin.text.Regex ${'$'}pattern${'$'}1
        """.trimMargin()),
            "class should contain static field definition" +
                    "\n\n$javapOutput"
        )
    }

    private fun summariseMethodInstructions(targetClass: String): Pair<Map<String, List<String>>, String> {
        val summarisedInstructions = mutableMapOf<String, List<String>>()
        val javapOutput = compilerEnvironment.runJavap(targetClass)
        val lineIterator = javapOutput.lines().iterator()
        val methodPattern = Regex(""" {2}(?:[a-z]+ )*([a-zA-Z_$][a-zA-Z_0-9$.]*)\((.*)\);""")
        val instructionPattern = Regex(""" *\d+: ([a-zA-Z_$][a-zA-Z_0-9$]*)\s*[#0-9, ]*\s*(?:// (.+))?""")
        while (lineIterator.hasNext()) {
            val outerLine = lineIterator.next()
            val methodMatch = methodPattern.matchEntire(outerLine) ?: continue
            val (methodName, signature) = methodMatch.destructured
            val methodInstructions = mutableListOf<String>()
            while (lineIterator.hasNext()) {
                val innerLine = lineIterator.next()
                if (innerLine.isBlank()) break
                val instructionMatch = instructionPattern.matchEntire(innerLine) ?: continue
                val instruction = instructionMatch.groupValues[1]
                val comment = if (instructionMatch.groupValues.size > 2) instructionMatch.groupValues[2] else null
                methodInstructions += if (comment != null) "$instruction $comment" else instruction
            }
            summarisedInstructions[methodName] = methodInstructions
        }
        return summarisedInstructions to javapOutput
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

        fun runJavap(targetClass: String): String {
            val process = Runtime.getRuntime().exec(
                arrayOf(
                    "javap",
                    "-p",
                    "-c",
                    "-classpath",
                    tempDirectory.toString(),
                    targetClass
                )
            )
            val errorLines = process.errorStream.bufferedReader().readLines()
            val text = process.inputStream.bufferedReader().readText()
            check(process.waitFor() == 0) { "javap failed\n${errorLines.joinToString("\n")}" }
            return text
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
