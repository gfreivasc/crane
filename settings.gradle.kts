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
  ":crane-router-tests:tests",
  ":crane-router-tests:fake-android",
  ":crane-router-tests:dummy-module-a",
  ":crane-router-tests:dummy-module-b",
  ":samples:basic",
  ":samples:complete",
)
