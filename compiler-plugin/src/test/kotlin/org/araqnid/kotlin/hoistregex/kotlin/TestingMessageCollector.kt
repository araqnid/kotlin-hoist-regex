package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector

object TestingMessageCollector : MessageCollector {
    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
        val prefix = if (location == null) "" else "(${location.path}:${location.line}:${location.column}) "
        if (severity == CompilerMessageSeverity.ERROR) {
            throw AssertionError(prefix + message)
        }
        println("$severity: $prefix$message")
    }

    override fun clear() {
    }

    override fun hasErrors(): Boolean = false
}
