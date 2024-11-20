@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import org.gradle.tooling.UnsupportedVersionException

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

if (JavaVersion.current() < JavaVersion.VERSION_21) throw UnsupportedVersionException("Please use Java 21+!")

rootProject.name = "build-logic"
