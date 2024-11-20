import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.internal.file.FileOperations
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject

class BuildLogicSettings @Inject constructor(private val fileOperations: FileOperations) : Plugin<Settings> {
    override fun apply(target: Settings): Unit = with(target) {
        dependencyResolutionManagement.apply {
            versionCatalogs.apply {
                val libs = maybeCreate("libs")
                val maybeFile = rootDir.resolveRoot("gradle/libs.versions.toml")
                if (maybeFile != rootDir.resolve("gradle/libs.versions.toml"))
                    libs.from(fileOperations.immutableFiles(maybeFile))

                val overrideLibsVersionsPropertyPrefix = "override.libs.versions."
                providers.gradlePropertiesPrefixedBy(overrideLibsVersionsPropertyPrefix).get().forEach { key, value ->
                    val alias = key.substringAfter(overrideLibsVersionsPropertyPrefix).replace(".", "-")
                    libs.overrideVersion(this@with, alias, value)
                }
            }
        }
    }

    private fun File.resolveRoot(relative: String): File = resolve(relative).takeIf { file -> file.exists() }
        ?: parentFile?.resolveRoot(relative) ?: throw FileNotFoundException()

    private fun VersionCatalogBuilder.overrideVersion(settings: Settings, alias: String, version: String) {
        version(alias, version)
        println(buildString {
            append("Overridden Version Catalogs")
            append("\u001b[95m libs.versions.toml\u001b[0m \u001b[93m[versions]\u001b[0m ")
            append("[ alias: \u001b[94m$alias\u001b[0m version: \u001b[92m$version\u001b[0m ]")
        })
    }
}
