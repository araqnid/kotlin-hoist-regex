package org.araqnid.kotlin.hoistregex.kotlin

import org.jetbrains.kotlin.js.config.JsConfig

object JsReporter : JsConfig.Reporter() {
    override fun warning(message: String) {
        println("warning: $message")
    }

    override fun error(message: String) {
        throw AssertionError(message)
    }
}
