plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        minSdkVersion(1)
        targetSdkVersion(30)
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Deps.Android.appcompat)
    implementation(Deps.Android.coreKtx)

    testImplementation(Deps.Testing.jUnit)
    testImplementation(Deps.Testing.assertJ)
    testImplementation(Deps.Testing.mockK)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.findByName("release"))

                artifactId = "crane"
                groupId = project.findProperty("library.groupId") as String
                version = project.findProperty("library.version") as String
            }
        }
    }
}
