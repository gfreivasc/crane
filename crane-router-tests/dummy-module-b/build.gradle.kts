plugins {
  kotlin("jvm")
  id("com.google.devtools.ksp")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {
  implementation(project(":crane-router-tests:fake-android"))
  implementation(project(":crane-annotations"))
  implementation(project(":crane-router-tests:dummy-module-a"))
  implementation(kotlin("stdlib"))
  ksp(project(":crane-router"))
}
