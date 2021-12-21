plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("kotlin-parcelize")
  id("kotlin-android")
}

android {
  compileSdk = 31
  buildToolsVersion = "31.0.0"

  defaultConfig {
    minSdk = 24
    targetSdk = 31

    applicationId = "com.gabrielfv.crane.basicsample"
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    vectorDrawables.useSupportLibrary = true
  }

  sourceSets {
    named("main") {
      java.srcDirs("build/generated/ksp/main/kotlin")
    }
    named("debug") {
      java.srcDirs("build/generated/ksp/debug/kotlin")
    }
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {

  implementation(project(":crane"))
  kapt(project(":crane-router"))
  implementation(kotlin("stdlib"))
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.appcompat:appcompat:1.4.0")
  implementation("androidx.fragment:fragment-ktx:1.4.0")
  implementation("com.google.android.material:material:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.2")
  implementation("androidx.legacy:legacy-support-v4:1.0.0")



  implementation("com.google.dagger:dagger:2.40.5")
  kapt("com.google.dagger:dagger-compiler:2.40.5")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
