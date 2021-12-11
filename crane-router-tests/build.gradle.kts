plugins {
  kotlin("jvm")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation(project(":crane-router"))
  implementation(project(":crane-annotations"))
  implementation(kotlin("stdlib"))
  implementation(libs.ksp.api)
  implementation(libs.google.auto.common)
  implementation(libs.androidx.room.processing)
  implementation(libs.kotlin.compile.core)
  implementation(libs.kotlin.compile.ksp)
  implementation(libs.junit)
  implementation(libs.assertj)

  testImplementation(libs.ksp.impl)
}
