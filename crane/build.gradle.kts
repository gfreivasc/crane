plugins {
  id("com.android.library")
  kotlin("android")
  id("maven-publish")
}

android {
  compileSdk = 31
  buildToolsVersion = "31.0.0"

  defaultConfig {
    minSdk = 1
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
  implementation(Deps.Android.appcompat)
  implementation(Deps.Android.coreKtx)

  testImplementation(Deps.Testing.jUnit)
  testImplementation(Deps.Testing.assertJ)
  testImplementation(Deps.Testing.mockK)
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
