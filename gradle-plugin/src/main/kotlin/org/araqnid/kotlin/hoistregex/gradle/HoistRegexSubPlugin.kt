package org.araqnid.kotlin.hoistregex.gradle

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@AutoService(KotlinGradleSubplugin::class)
class HoistRegexSubPlugin : KotlinGradleSubplugin<AbstractCompile> {
    override fun isApplicable(project: Project, task: AbstractCompile): Boolean =
        project.plugins.hasPlugin(HoistRegexPlugin::class.java)

    override fun getCompilerPluginId(): String = "org.araqnid.hoist-regex"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact("org.araqnid.hoist-regex", "kotlin-hoist-regex-plugin", "0.0.1")

    override fun apply(
        project: Project,
        kotlinCompile: AbstractCompile,
        javaCompile: AbstractCompile?,
        variantData: Any?,
        androidProjectHandler: Any?,
        kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
    ): List<SubpluginOption> {
        val extension = project.extensions.findByType(HoistRegexGradleExtension::class.java) ?: HoistRegexGradleExtension()

        val enabledOption = SubpluginOption(key = "enabled", value = extension.enabled.toString())

        return listOf(enabledOption)
    }
}
