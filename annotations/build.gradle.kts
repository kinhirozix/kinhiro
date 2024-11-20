plugins {
    kotlin("jvm")
    id("me.kinhiro.composite.code")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    api(libs.annotations)
    api(libs.spotbugs.annotations)
}
