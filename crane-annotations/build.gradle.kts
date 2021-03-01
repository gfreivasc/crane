plugins {
  kotlin("jvm")
  id("maven-publish")
}

dependencies {
  kotlin("stdlib")
}

afterEvaluate {
  publishing {
    publications {
      create<MavenPublication>("maven") {
        from(components.findByName("java"))

        artifactId = "crane-annotations"
        groupId = project.findProperty("library.groupId") as String
        version = project.findProperty("library.version") as String
      }
    }
  }
}
