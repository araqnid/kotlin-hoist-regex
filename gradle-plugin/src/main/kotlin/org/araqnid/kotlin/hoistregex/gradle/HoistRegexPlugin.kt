package org.araqnid.kotlin.hoistregex.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class HoistRegexPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create("hoistRegex", HoistRegexGradleExtension::class.java)
    }
}

open class HoistRegexGradleExtension {
    var enabled = true
}
