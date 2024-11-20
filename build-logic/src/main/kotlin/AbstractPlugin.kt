import org.gradle.api.Plugin
import org.gradle.api.Project

abstract class AbstractPlugin : Plugin<Project> {
    abstract fun Project.applyPlugin()
    final override fun apply(project: Project): Unit = with(project) {
        applyPlugin()
    }
}
