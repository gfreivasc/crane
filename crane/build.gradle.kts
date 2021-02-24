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
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation("org.assertj:assertj-core:3.19.0")
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
