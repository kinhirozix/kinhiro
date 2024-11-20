import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.gradle.plugins.signing.SigningExtension
import javax.inject.Inject

class PublishPlugin : AbstractPlugin() {
    override fun Project.applyPlugin() {
        val publishExtension = extensions.create<PublishExtension>("publish", this)
        val mavenExtension = publishExtension.extensions.create<MavenExtension>("maven", this)
        val mavenPomExtension = mavenExtension.extensions.create<MavenPomSpec>("pom", this)
        val mavenPomLicenseExtension = mavenPomExtension.extensions.create<MavenPomLicenseSpec>("license", this)
        val mavenPomDeveloperExtension = mavenPomExtension.extensions.create<MavenPomDeveloperSpec>("developer", this)
        val mavenPomScmExtension = mavenPomExtension.extensions.create<MavenPomScmSpec>("scm", this)
        val signingExtension = publishExtension.extensions.create<PublishSigningExtension>("signing", this)

        plugins.apply("maven-publish")
        plugins.apply("signing")

        extensions.configure<PublishingExtension> {
            repositories { repo ->
                repo.mavenLocal()
            }

            publications { publication ->
                publication.create<MavenPublication>(name) {
                    groupId = mavenExtension.groupId.get()
                    artifactId = mavenExtension.artifactId.get()
                    version = mavenExtension.version.get()
                    from(components["java"])

                    pom { pom ->
                        pom.name.set(mavenPomExtension.name)
                        pom.description.set(mavenPomExtension.description)
                        pom.url.set(mavenPomExtension.url)

                        pom.licenses { licenses ->
                            licenses.license { license ->
                                license.name.set(mavenPomLicenseExtension.name)
                                license.url.set(mavenPomLicenseExtension.url)
                            }
                        }

                        pom.developers { developers ->
                            developers.developer { developer ->
                                developer.id.set(mavenPomDeveloperExtension.id)
                                developer.name.set(mavenPomDeveloperExtension.name)
                                developer.email.set(mavenPomDeveloperExtension.email)
                            }
                        }

                        pom.scm { scm ->
                            scm.connection.set(mavenPomScmExtension.connection)
                            scm.developerConnection.set(mavenPomScmExtension.development)
                            scm.url.set(mavenPomScmExtension.url)
                        }
                    }
                }
            }
        }

        val publishingExtension = extensions.getByType<PublishingExtension>()

        extensions.configure<SigningExtension> {
            useInMemoryPgpKeys(
                signingExtension.id.get(),
                signingExtension.key.get(),
                signingExtension.password.get()
            )

            sign(publishingExtension.publications)
        }
    }
}

private val Project.publishExtension: PublishExtension get() = extensions.getByType()
private val Project.mavenExtension: MavenExtension get() = publishExtension.extensions.getByType()
private val Project.mavenPomExtension: MavenPomSpec get() = mavenExtension.extensions.getByType()
private val Project.mavenPomLicenseExtension: MavenPomLicenseSpec get() = mavenPomExtension.extensions.getByType()
private val Project.mavenPomDeveloperExtension: MavenPomDeveloperSpec get() = mavenPomExtension.extensions.getByType()
private val Project.mavenPomScmExtension: MavenPomScmSpec get() = mavenPomExtension.extensions.getByType()
private val Project.signingExtension: PublishSigningExtension get() = publishExtension.extensions.getByType()

abstract class PublishExtension @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) : ExtensionAware {
    fun automaticConfigureMaven(signing: Boolean = false) {
        val groupId = project.findProperty("maven.group")?.toString() ?: project.group.toString()
        project.mavenExtension.groupId.set(groupId)
        val artifactId = project.findProperty("maven.name")?.toString() ?: project.name
        project.mavenExtension.artifactId.set(artifactId)
        val version = project.findProperty("maven.version")?.toString() ?: project.version.toString()
        project.mavenExtension.version.set(version)
        val name = project.findProperty("maven.pom.name")?.toString()
            ?: project.name.split("-").joinToString(" ") { s -> s.uppercaseFirstChar() }
        project.mavenPomExtension.name.set(name)
        val description = project.findProperty("maven.pom.description")?.toString() ?: project.description ?: ""
        project.mavenPomExtension.description.set(description)
        val url = project.findProperty("maven.pom.url")?.toString() ?: SOURCES_URL
        project.mavenPomExtension.url.set(url)
        val licenseName = project.findProperty("maven.license.name")?.toString() ?: LICENSE_NAME
        project.mavenPomLicenseExtension.name.set(licenseName)
        val licenseUrl = project.findProperty("maven.license.url")?.toString() ?: LICENSE_URL
        project.mavenPomLicenseExtension.url.set(licenseUrl)
        val developerId = project.findProperty("maven.developer.id")?.toString() ?: "kinhirozix"
        project.mavenPomDeveloperExtension.id.set(developerId)
        val developerName = project.findProperty("maven.developer.name")?.toString() ?: "Kinhiro Zix"
        project.mavenPomDeveloperExtension.name.set(developerName)
        val developerEmail = project.findProperty("maven.developer.email")?.toString() ?: "kinhirozix@outlook.com"
        project.mavenPomDeveloperExtension.email.set(developerEmail)
        val scmConnection = project.findProperty("maven.scm.connection")?.toString() ?: SCM_CONN_URL
        project.mavenPomScmExtension.connection.set(scmConnection)
        val scmDevConnection = project.findProperty("maven.scm.development")?.toString() ?: SCM_DEV_URL
        project.mavenPomScmExtension.development.set(scmDevConnection)
        val scmUrl = project.findProperty("maven.scm.url")?.toString() ?: SCM_URL
        project.mavenPomScmExtension.url.set(scmUrl)

        if (signing) {
            val key = project.findProperty("signing.key")?.toString()
                ?: project.providers.environmentVariable("SIGNING_KEY").orNull
                ?: throw IllegalStateException("Signing key is missing!")
            project.signingExtension.id.set(key)
            val secret = project.findProperty("signing.secret")?.toString()
                ?: System.getenv("SIGNING_SECRET")
                ?: throw IllegalStateException("Signing secret is missing!")
            project.signingExtension.key.set(secret)
            val password = project.findProperty("signing.password")?.toString()
                ?: System.getenv("SIGNING_PASSWORD")
                ?: throw IllegalStateException("Signing password is missing!")
            project.signingExtension.password.set(password)
        }
    }
}

abstract class MavenExtension @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) : ExtensionAware {
    val groupId: Property<String> get() = objects.property<String>().convention(project.group.toString())
    val artifactId: Property<String> get() = objects.property<String>().convention(project.name)
    val version: Property<String> get() = objects.property<String>().convention(project.version.toString())
}

abstract class MavenPomSpec @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) : ExtensionAware {
    val name: Property<String> get() = objects.property<String>().convention(project.name.toMavenPomName())
    val description: Property<String> get() = objects.property<String>().convention(project.description ?: "")
    val url: Property<String> get() = objects.property<String>().convention(SOURCES_URL)
}

abstract class MavenPomLicenseSpec @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) : ExtensionAware {
    val name: Property<String> get() = objects.property<String>().convention(LICENSE_NAME)
    val url: Property<String> get() = objects.property<String>().convention(LICENSE_URL)
}

abstract class MavenPomDeveloperSpec @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) : ExtensionAware {
    val id: Property<String> get() = objects.property<String>().convention("kinhirozix")
    val name: Property<String> get() = objects.property<String>().convention("Kinhiro Zix")
    val email: Property<String> get() = objects.property<String>().convention("kinhirozix@outlook.com")
}

abstract class MavenPomScmSpec @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) : ExtensionAware {
    val connection: Property<String> get() = objects.property<String>().convention(SCM_CONN_URL)
    val development: Property<String> get() = objects.property<String>().convention(SCM_DEV_URL)
    val url: Property<String> get() = objects.property<String>().convention(SCM_URL)
}

abstract class PublishSigningExtension @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) : ExtensionAware {
    val id: Property<String> get() = objects.property<String>().convention(System.getenv("SIGNING_KEY_ID"))
    val key: Property<String> get() = objects.property<String>().convention(System.getenv("SIGNING_KEY"))
    val password: Property<String> get() = objects.property<String>().convention(System.getenv("SIGNING_PASSWORD"))
}

private fun String.toMavenPomName(): String = split("-").joinToString(" ") { s -> s.uppercaseFirstChar() }
