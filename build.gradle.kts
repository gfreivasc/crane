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
    maven { setUrl("/Users/gabriel.vasconcelos/ASProjects/androidx/build/support_repo/") }
  }
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}
