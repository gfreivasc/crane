buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath(kotlin("gradle-plugin", version = Versions.Kotlin.lang))
    classpath(Deps.Android.gradle)
    classpath(Deps.KSP.classpath)
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    maven { setUrl("https://androidx.dev/snapshots/builds/7204104/artifacts/repository") }
  }
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}
