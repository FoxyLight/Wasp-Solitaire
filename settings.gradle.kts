pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        // Alternative official Maven Central CDN, for intermittent repo.maven.apache.org failures.
        maven(url = "https://repo1.maven.org/maven2/")
        mavenCentral()
    }
}
rootProject.name = "WaspSolitaire"
include(":app", ":game-engine")
