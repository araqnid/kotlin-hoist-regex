package org.araqnid.kotlin.hoistregex.kotlin

import com.google.auto.service.AutoService
import org.araqnid.kotlin.hoistregex.kotlin.ConfigurationKeys.KEY_ENABLED
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(CommandLineProcessor::class)
class HoistRegexCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "hoist-regex"

    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption("enabled", "true|false", "whether plugin is enabled")
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        when (option.optionName) {
            "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
            else -> error("Unexpected config option ${option.optionName}")
        }
    }
}
