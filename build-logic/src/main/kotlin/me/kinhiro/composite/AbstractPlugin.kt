package me.kinhiro.composite

import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class AbstractPlugin : Plugin<Project> {
    abstract fun Project.applyPlugin()
    final override fun apply(project: Project) = project.applyPlugin()
}
