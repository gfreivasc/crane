buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.4.30-dev-experimental-20210205")
        classpath(kotlin("gradle-plugin", version = "1.4.30"))
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
