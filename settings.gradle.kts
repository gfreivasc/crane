rootProject.name = "crane-lib"  // Required for project accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  repositories {
    google()
    mavenCentral()
  }
}
include(
  ":crane",
  ":samples:basic",
)
