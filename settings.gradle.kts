@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import org.gradle.tooling.UnsupportedVersionException

pluginManagement {
    includeBuild("./build-logic")

    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    repositories {
        mavenCentral()
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("me.kinhiro.composite.settings")
}

if (JavaVersion.current() < JavaVersion.VERSION_21) throw UnsupportedVersionException("Please use Java 21+!")

rootProject.name = "kinhiro"

include(
    ":annotations"
)
