buildscript {
  repositories {
    mavenCentral()
    google()
  }
  dependencies {
    classpath(kotlin("gradle-plugin", version = Versions.Kotlin.lang))
    classpath(Deps.Android.gradle)
    classpath(Deps.KSP.classpath)
  }
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}
