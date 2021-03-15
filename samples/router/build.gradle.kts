plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("kotlin-parcelize")
//  id("com.google.devtools.ksp")
}

android {
  compileSdkVersion(30)
  buildToolsVersion("30.0.3")

  defaultConfig {
    minSdkVersion(16)
    targetSdkVersion(30)

    applicationId("com.gabrielfv.crane.routersample")
    versionCode(1)
    versionName("1.0")

    testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")

    vectorDrawables.useSupportLibrary = true
  }

  buildFeatures {
    viewBinding = true
  }

  buildTypes {
    named("release") {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
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
  implementation(project(":crane"))
  implementation(project(":crane-annotations"))
  kapt(project(":crane-router"))

  implementation(kotlin("stdlib"))
  implementation("androidx.core:core-ktx:1.3.2")
  implementation("androidx.appcompat:appcompat:1.2.0")
  implementation("com.google.android.material:material:1.3.0")
  implementation("androidx.fragment:fragment-ktx:1.3.1")
  implementation("androidx.constraintlayout:constraintlayout:2.0.4")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
