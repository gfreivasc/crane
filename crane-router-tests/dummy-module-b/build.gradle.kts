plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ksp)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation(projects.craneRouterTests.fakeAndroid)
  implementation(projects.craneRouterTests.dummyModuleA)
  implementation(projects.craneAnnotations)
  implementation(kotlin("stdlib"))
  ksp(projects.craneRouter)
}
