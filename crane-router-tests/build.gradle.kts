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
  implementation(Deps.KSP.api)
  implementation(Deps.GoogleAuto.common)
  implementation(Deps.AndroidProcessing.room)
  implementation(Deps.Testing.KotlinCompile.base)
  implementation(Deps.Testing.KotlinCompile.ksp)
  implementation(Deps.Testing.jUnit)
  implementation(Deps.Testing.assertJ)

  testImplementation(Deps.KSP.impl)
}
