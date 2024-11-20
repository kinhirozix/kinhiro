import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import java.nio.charset.Charset

class CodeCheckPlugin : AbstractPlugin() {
    override fun Project.applyPlugin() {
        plugins.apply(SpotlessPlugin::class.java)
        extensions.configure<SpotlessExtension> {
            kotlin { kotlin ->
                rootProject.file("spotless/license-header")
                    .bufferedReader(Charset.forName("UTF-8"))
                    .use { reader ->
                        val licenseHeader = reader.readText()
                            .replace("{year}", providers.gradleProperty("spotless.license.year").get())
                            .replace("{owner}", providers.gradleProperty("spotless.license.owner").get())
                        kotlin.licenseHeader(licenseHeader, "^(@|package)")
                    }

                kotlin.target("src/main/kotlin/**/*.kt")
                kotlin.trimTrailingWhitespace()
                kotlin.endWithNewline()
            }

            java { java ->
                rootProject.file("spotless/license-header")
                    .bufferedReader(Charset.forName("UTF-8"))
                    .use { reader ->
                        val licenseHeader = reader.readText()
                            .replace("{year}", providers.gradleProperty("spotless.license.year").get())
                            .replace("{owner}", providers.gradleProperty("spotless.license.owner").get())
                        java.licenseHeader(licenseHeader, "^(package)")
                    }

                java.target("src/main/java/**/*.java")
                java.trimTrailingWhitespace()
                java.endWithNewline()
            }
        }

        tasks.named("check").dependsOn(tasks.named("spotlessCheck"))
        tasks.named("assemble").dependsOn(tasks.named("spotlessApply"))
    }
}
