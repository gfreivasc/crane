plugins {
  alias(libs.plugins.kotlin.jvm)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation(projects.craneRouter)
  implementation(projects.craneAnnotations)
  implementation(kotlin("stdlib"))
  implementation(libs.ksp.api)
  implementation(libs.google.auto.common)
  implementation(libs.androidx.room.processing)
  implementation(libs.kotlin.compile.core)
  implementation(libs.kotlin.compile.ksp)
  implementation(libs.junit)
  implementation(libs.assertj)

  testImplementation(projects.craneRouterTests.fakeAndroid)
  testImplementation(projects.craneRouterTests.dummyModuleA)
  testImplementation(projects.craneRouterTests.dummyModuleB)
  testImplementation(libs.ksp.impl)
}
