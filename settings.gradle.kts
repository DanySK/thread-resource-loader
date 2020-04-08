import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

rootProject.name = "thread-inheritable-resource-loader"

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard:dependencies:+")
}

bootstrapRefreshVersionsAndDependencies()
