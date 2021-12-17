plugins {
  kotlin("jvm")
  kotlin("kapt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation(project(":crane-router-tests:fake-android"))
  implementation(project(":crane-annotations"))
  implementation(kotlin("stdlib"))
  kapt(project(":crane-router"))

  testImplementation(libs.ksp.impl)
}
