plugins {
  alias(libs.plugins.android.lib)
  alias(libs.plugins.kotlin.android)
  id("maven-publish")
}

android {
  compileSdk = 34
  buildToolsVersion = "34.0.0"
  namespace = "com.gabrielfv.crane"

  defaultConfig {
    minSdk = 11
  }
  testOptions {
    unitTests.isReturnDefaultValues = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  api(projects.craneAnnotations)
  implementation(kotlin("stdlib"))
  implementation(libs.bundles.androidx)

  testImplementation(libs.junit)
  testImplementation(libs.assertj)
  testImplementation(libs.mockk)
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("release") {
        from(components.findByName("release"))

        artifactId = "crane"
        groupId = project.findProperty("library.groupId") as String
        version = project.findProperty("library.version") as String
      }
    }
  }
}
