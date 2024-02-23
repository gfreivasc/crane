plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.kotlin.kapt)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {
  implementation(projects.craneRouterTests.fakeAndroid)
  implementation(projects.craneAnnotations)
  implementation(kotlin("stdlib"))
  kapt(projects.craneRouter)

  testImplementation(libs.ksp.impl)
}
