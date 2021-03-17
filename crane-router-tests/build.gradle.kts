plugins {
  // Enable android and parcelize classpath for testing purposes
  id("com.android.library")
  kotlin("android")
  kotlin("kapt")
}

android {
  defaultConfig {
    compileSdk = 30
    targetSdk = 30
    minSdk = 1
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
  implementation(Deps.Testing.KotlinCompile.base)
  implementation(Deps.Testing.KotlinCompile.ksp)
  implementation(Deps.Testing.jUnit)
  implementation(Deps.Testing.assertJ)

  testImplementation(Deps.KSP.impl)
}
