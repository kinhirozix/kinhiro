plugins {
    kotlin("jvm")
    id("me.kinhiro.composite.code")
    id("me.kinhiro.composite.publish")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    api(libs.annotations)
    api(libs.spotbugs.annotations)
}

publish.automaticConfigureMaven()

publish {
    maven
}
