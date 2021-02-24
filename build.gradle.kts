buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(Deps.Android.gradle)
        classpath(Deps.KSP.classpath)
        classpath(kotlin("gradle-plugin", version = Versions.Kotlin.lang))
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
