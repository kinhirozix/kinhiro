plugins {
    kotlin("jvm")
    id("me.kinhiro.composite.code")
    `maven-publish`
}

dependencies {
    implementation(libs.kotlin.stdlib)
    api(libs.annotations)
    api(libs.spotbugs.annotations)
}

publishing {
    publications {
        create<MavenPublication>(project.name) {
            groupId = findProperties("maven.group") ?: project.group.toString()
            artifactId = findProperties("maven.name") ?: project.name
            version = findProperties("maven.version") ?: project.version.toString()
            from(components["java"])
        }
    }
}
