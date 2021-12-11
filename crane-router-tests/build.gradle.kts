plugins {
  // Enable android and parcelize classpath for testing purposes
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {
  defaultConfig {
    compileSdk = 31
    targetSdk = 31
    minSdk = 1
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
  implementation(project(":crane-router"))
  implementation(project(":crane"))
  implementation(project(":crane-annotations"))
  implementation(kotlin("stdlib"))
  implementation(Deps.Android.appcompat)
  implementation(Deps.KSP.api)
  implementation(Deps.GoogleAuto.common)
  implementation(Deps.AndroidProcessing.room)
  implementation(Deps.Testing.KotlinCompile.base)
  implementation(Deps.Testing.KotlinCompile.ksp)
  implementation(Deps.Testing.jUnit)
  implementation(Deps.Testing.assertJ)

  testImplementation(Deps.KSP.impl)
}
