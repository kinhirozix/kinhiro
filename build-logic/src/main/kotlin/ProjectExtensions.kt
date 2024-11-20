import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: LibrariesForLibs get() = extensions.getByType()
internal val Project.base: BasePluginExtension get() = extensions.getByType()
fun Project.findProperties(name: String): String? = findProperty(name)?.toString()
