plugins {
  id("com.android.application")
  kotlin("android")
  id("kotlin-parcelize")
  id("kotlin-android")
}

android {
  compileSdk = 30
  buildToolsVersion = "31.0.0"
  namespace = "com.gabrielfv.basicsample"

  defaultConfig {
    minSdk = 16
    targetSdk = 30

    applicationId = "com.gabrielfv.crane.basicsample"
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"
  }
}

dependencies {

  implementation(project(":crane"))
  implementation(kotlin("stdlib"))
  implementation("androidx.core:core-ktx:1.3.2")
  implementation("androidx.appcompat:appcompat:1.2.0")
  implementation("androidx.fragment:fragment-ktx:1.3.0")
  implementation("com.google.android.material:material:1.3.0")
  implementation("androidx.constraintlayout:constraintlayout:2.0.4")
  implementation("androidx.legacy:legacy-support-v4:1.0.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.2")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
