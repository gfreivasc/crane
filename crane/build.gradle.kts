plugins {
  alias(libs.plugins.android.lib)
  alias(libs.plugins.kotlin.android)
  id("maven-publish")
}

android {
  compileSdk = 35
  buildToolsVersion = "35.0.0"
  namespace = "com.gabrielfv.crane"

  defaultConfig {
    minSdk = 11
  }
  testOptions {
    unitTests.isReturnDefaultValues = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlinOptions {
    jvmTarget = "21"
  }
}

dependencies {
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
