dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven { setUrl("https://androidx.dev/snapshots/builds/7285896/artifacts/repository") }
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
