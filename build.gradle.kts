@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("me.kinhiro.composite.root")
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    group = property("maven.group").toString()
    version = property("maven.version").toString()
}

subprojects {
    /**
     * Retrieves the Java version by checking the local build environment.
     * The value is determined as follows (in order of precedence):
     * 1. The value of the Gradle property `java.version`, if set.
     * 2. The value of the `java` key in the `versions` table of the root project's
     * `./gradle/libs.versions.toml`, if this file exists, the table is defined and key is set.
     * 3. The default hardcoded value of `21`.
     */
    val javaVersion = providers.gradleProperty("java.version").orNull
        ?: rootProject.libs.versions.java.getOrElse("21")

    pluginManager.withPlugin("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
            sourceCompatibility = JavaVersion.toVersion(javaVersion)
            targetCompatibility = JavaVersion.toVersion(javaVersion)
            withSourcesJar()
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release = javaVersion.toInt()
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }

        tasks.named<Jar>("jar") {
            from(rootProject.file("LICENSE")).rename("LICENSE", "LICENSE.txt")
        }

        tasks.named<Jar>("sourcesJar") {
            from(rootProject.file("LICENSE")).rename("LICENSE", "LICENSE.txt")
        }
    }

    /**
     * Retrieves the Kotlin version by checking the local build environment.
     * The value is determined as follows (in order of precedence):
     * 1. The value of the Gradle property `kotlin.version`, if set.
     * 2. The value of the `kotlin` key in the `versions` table of the root project's
     * `./gradle/libs.versions.toml`, if this file exists, the table is defined and key is set.
     * 3. The default hardcoded value of `2.0.21`.
     */
    val kotlinVersion = providers.gradleProperty("kotlin.version").orNull
        ?: rootProject.libs.versions.kotlin.getOrElse("2.0.21")
    val kotlinShortVersion = Regex("^(\\d+\\.\\d+)").find(kotlinVersion)?.groupValues?.first() ?: "2.0"

    pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm.id) {
        extensions.configure<KotlinJvmProjectExtension> {
            sourceSets.all {
                languageSettings {
                    if (providers.gradleProperty("kotlin.settings.enabled").getOrElse("false").toBoolean()) {
                        apiVersion = kotlinShortVersion
                        languageVersion = kotlinShortVersion
                    }

                    optIn("kotlin.RequiresOptIn")
                }
            }
        }

        tasks.withType<KotlinCompile> {
            compilerOptions {
                jvmTarget = JvmTarget.fromTarget(javaVersion)
                freeCompilerArgs = freeCompilerArgs.get() + listOf("-Xjvm-default=all")
            }
        }
    }
}
