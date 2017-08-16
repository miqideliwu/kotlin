@file:Suppress("unused") // usages in build scripts are not tracked properly

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.*
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.AbstractTask
import org.gradle.jvm.tasks.Jar
import java.io.File

inline fun <reified T : Task> Project.task(noinline configuration: T.() -> Unit) = tasks.creating(T::class, configuration)


fun AbstractTask.dependsOnTaskIfExists(task: String) {
    project.tasks.firstOrNull { it.name == task }?.let { dependsOn(it) }
}

fun AbstractTask.dependsOnTaskIfExistsRec(task: String, project: Project? = null) {
    dependsOnTaskIfExists(task)
    (project ?: this.project).subprojects.forEach {
        dependsOnTaskIfExistsRec(task, it)
    }
}

fun Jar.setupRuntimeJar(implementationTitle: String): Unit {
    dependsOn(":prepare:build.version:prepare")
    manifest.attributes.apply {
        put("Built-By", project.rootProject.extra["manifest.impl.vendor"])
        put("Implementation-Vendor", project.rootProject.extra["manifest.impl.vendor"])
        put("Implementation-Title", implementationTitle)
        put("Implementation-Version", project.rootProject.extra["build.number"])
    }
//    from(project.configurations.getByName("build-version").files, action = { into("META-INF/") })
}

fun Jar.setupSourceJar(implementationTitle: String): Unit {
    dependsOn("classes")
    setupRuntimeJar(implementationTitle + " Sources")
    project.pluginManager.withPlugin("java-base") {
        from(project.the<JavaPluginConvention>().sourceSets["main"].allSource)
    }
    classifier = "sources"
    project.artifacts.add("archives", this)
}


inline fun<T: Any> Project.withJavaPlugin(crossinline body: () -> T?): T? {
    var res: T? = null
    pluginManager.withPlugin("java") {
        res = body()
    }
    return res
}

fun Project.getCompiledClasses(): SourceSetOutput? = withJavaPlugin {
    the<JavaPluginConvention>().sourceSets.getByName("main").output
}

fun Project.getSources(): SourceDirectorySet? = withJavaPlugin {
    the<JavaPluginConvention>().sourceSets.getByName("main").allSource
}

fun Project.getResourceFiles(): SourceDirectorySet? = withJavaPlugin {
    the<JavaPluginConvention>().sourceSets.getByName("main").resources
}



fun File(root: File, vararg children: String): File = children.fold(root, { f, c -> File(f, c) })
fun File(root: String, vararg children: String): File = children.fold(File(root), { f, c -> File(f, c) })
