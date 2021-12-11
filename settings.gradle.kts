dependencyResolutionManagement {
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
  ":samples:router"
)
