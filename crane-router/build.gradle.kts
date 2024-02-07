plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("maven-publish")
}

group = project.findProperty("library.groupId") as String
version = project.findProperty("library.version") as String

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "11"
  }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-Xopt-in=androidx.room.compiler.processing.ExperimentalProcessingApi",
      "-Xjvm-default=enable",
    )
  }
}

dependencies {
  implementation(projects.craneAnnotations)
  implementation(kotlin("stdlib"))
  implementation(kotlin("compiler-embeddable"))
  implementation(libs.ksp.api)
  implementation(libs.androidx.room.processing)
  implementation(libs.google.auto.service.annotations)
  implementation(libs.google.auto.common)
  implementation(libs.square.javapoet)
  implementation(libs.square.kotlinpoet)
  implementation(libs.gradle.incap)
  compileOnly(libs.ksp.impl)
  compileOnly(libs.google.auto.service)
  kapt(libs.gradle.incap)
  kapt(libs.google.auto.service)
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("maven") {
        from(components.findByName("java"))

        artifactId = "crane-router"
        groupId = project.findProperty("library.groupId") as String
        version = project.findProperty("library.version") as String
      }
    }
  }
}
