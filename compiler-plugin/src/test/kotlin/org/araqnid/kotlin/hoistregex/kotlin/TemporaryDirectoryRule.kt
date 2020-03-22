package org.araqnid.kotlin.hoistregex.kotlin

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

open class TemporaryDirectoryRule : TestRule {
    lateinit var description: Description
    lateinit var tempDirectory: File
    val testMethodName by lazy { description.methodName!! }
    val testClassName by lazy { description.className!! }
    val testName by lazy { "$testClassName.$testMethodName" }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                this@TemporaryDirectoryRule.description = description
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
}
