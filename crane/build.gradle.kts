plugins {
    id("com.android.library")
    kotlin("android")
    id("internal.publishing")
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

internalPublishing {
    artifactId = "crane"
    groupId = findProperty("library.groupId") as String
    version = findProperty("library.version") as String
    versionSuffix = System.getenv("VERSION_SUFFIX").orEmpty()

    gitHub {
        repository = findProperty("library.repository") as String
        username = System.getenv("GITHUB_PUBLISH_USERNAME").orEmpty()
        password = System.getenv("GITHUB_PUBLISH_TOKEN").orEmpty()
    }
}
