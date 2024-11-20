plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-gradle-plugin`
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    composite(libs.kotlin.stdlib)
    composite(libs.kotlin.reflect)
    composite(libs.annotations)
    composite(gradleApi())
    composite(gradleKotlinDsl())
    composite(libs.spotless.plugin.gradle)
    composite(files(libs::class.java.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        create("settings") {
            id = "me.kinhiro.composite.settings"
            implementationClass = "BuildLogicSettings"
        }

        create("root") {
            id = "me.kinhiro.composite.root"
            implementationClass = "RootPlugin"
        }

        create("code") {
            id = "me.kinhiro.composite.code"
            implementationClass = "CodeCheckPlugin"
        }

        create("publish") {
            id = "me.kinhiro.composite.publish"
            implementationClass = "PublishPlugin"
        }
    }
}

fun DependencyHandler.composite(dependencyNotation: Any) {
    compileOnly(dependencyNotation)
    runtimeOnly(dependencyNotation)
}
