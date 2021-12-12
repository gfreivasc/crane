@file:Suppress("UnstableApiUsage")
enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}
include(
  ":crane",
  ":crane-annotations",
  ":crane-router",
  ":crane-router-tests",
  ":samples:basic",
  ":samples:router",
)
