plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("maven-publish")
}

group = project.findProperty("library.groupId") as String
version = project.findProperty("library.version") as String

dependencies {
  implementation(project(":crane-annotations"))
  implementation(kotlin("stdlib"))
  implementation(Deps.KSP.api)
  implementation(Deps.GoogleAuto.serviceAnnotations)
  implementation(Deps.Square.kotlinPoet)
  implementation(Deps.Square.javaPoet)
  implementation(Deps.Gradle.inCap)
  compileOnly(Deps.KSP.impl)
  compileOnly(Deps.GoogleAuto.service)
  kapt(Deps.Gradle.inCap)
  kapt(Deps.GoogleAuto.service)

  testImplementation(Deps.KSP.impl)
  testImplementation(Deps.Testing.KotlinCompile.base)
  testImplementation(Deps.Testing.KotlinCompile.ksp)
  testImplementation(Deps.Testing.mockK)
  testImplementation(Deps.Testing.jUnit)
  testImplementation(Deps.Testing.assertJ)
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
