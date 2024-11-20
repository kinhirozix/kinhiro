import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider

fun <T : Task> TaskProvider<T>.dependsOn(vararg tasks: TaskProvider<*>): Task = get().dependsOn(*tasks)
