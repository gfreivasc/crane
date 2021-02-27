plugins {
  id("com.google.devtools.ksp")
  kotlin("jvm")
  id("maven-publish")
}

group = project.findProperty("library.groupId") as String
version = project.findProperty("library.version") as String

dependencies {
  implementation(kotlin("stdlib"))
  implementation(Deps.KSP.api)
  implementation(Deps.AutoService.annotations)
  compileOnly(Deps.KSP.impl)
  ksp(Deps.AutoService.ksp)

  testImplementation(Deps.Testing.KotlinCompile.base)
  testImplementation(Deps.Testing.KotlinCompile.ksp)
  testImplementation(Deps.KSP.impl)
  testImplementation(Deps.Testing.jUnit)
  testImplementation(Deps.Testing.assertJ)
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("maven") {
        from(components.findByName("release"))

        artifactId = "crane-router"
        groupId = project.findProperty("library.groupId") as String
        version = project.findProperty("library.version") as String
      }
    }
  }
}
