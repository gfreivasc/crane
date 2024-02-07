plugins {
  id("com.android.library")
  kotlin("android")
  id("maven-publish")
}

android {
  compileSdk = 31
  buildToolsVersion = "31.0.0"
  namespace = "com.gabrielfv.crane"

  defaultConfig {
    minSdk = 11
    targetSdk = 31
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
  api(project(":crane-annotations"))
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
