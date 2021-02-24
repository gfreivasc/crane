plugins {
    kotlin("jvm") version "1.4.30"
    id("java-gradle-plugin")
}

repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies{
    implementation(kotlin("gradle-plugin-api"))
    implementation(gradleKotlinDsl())
    implementation("com.android.tools.build:gradle:4.1.2")
}

gradlePlugin {
    plugins {
        register("internalPublishing") {
            id = "internal.publishing"
            group = "com.gabrielfv"
            version = "0.1.0"
            implementationClass = "plugin.Publishing"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
