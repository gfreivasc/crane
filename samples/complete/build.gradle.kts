plugins {
  alias(libs.plugins.android.app)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.parcelize)
  alias(libs.plugins.kotlin.kapt)
}

android {
  compileSdk = 34
  buildToolsVersion = "34.0.0"
  namespace = "com.gabrielfv.samples.complete"

  defaultConfig {
    minSdk = 26 // java.time without desugar
    targetSdk = 34

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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
}

dependencies {

  implementation(projects.crane)
  kapt(projects.craneRouter)
  implementation(kotlin("stdlib"))
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.appcompat:appcompat:1.4.0")
  implementation("androidx.fragment:fragment-ktx:1.4.0")
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("com.google.android.material:material:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.2")
  implementation("androidx.legacy:legacy-support-v4:1.0.0")

  implementation("androidx.room:room-common:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")


  implementation("com.google.dagger:dagger:2.48.1")
  kapt("com.google.dagger:dagger-compiler:2.48.1")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
