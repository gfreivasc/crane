plugins {
  alias(libs.plugins.android.app)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.parcelize)
}

android {
  compileSdk = 35
  buildToolsVersion = "35.0.0"
  namespace = "com.gabrielfv.basicsample"

  defaultConfig {
    minSdk = 21
    targetSdk = 35

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
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlinOptions {
    jvmTarget = "21"
  }
}

dependencies {

  implementation(projects.crane)
  implementation(kotlin("stdlib"))
  implementation("androidx.core:core-ktx:1.15.0")
  implementation("androidx.appcompat:appcompat:1.7.0")
  implementation("androidx.fragment:fragment-ktx:1.8.6")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.constraintlayout:constraintlayout:2.2.1")
  implementation("androidx.legacy:legacy-support-v4:1.0.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
