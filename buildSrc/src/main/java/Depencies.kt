object Versions {
  object Kotlin {
    const val lang = "1.6.0"
    const val ksp = "1.6.0-1.0.1"
  }

  object Square {
    const val kotlinPoet = "1.10.2"
    const val javaPoet = "1.13.0"
  }

  object Android {
    const val gradle = "7.0.2"
    const val appcompat = "1.2.0"
    const val coreKtx = "1.3.2"
  }

  object AndroidProcessing {
    const val room = "2.4.0-rc01"
  }

  object Testing {
    const val jUnit = "4.13.2"
    const val mockK = "1.10.6"
    const val assertJ = "3.19.0"
    const val kotlinCompile = "1.4.6"
  }

  object GoogleAuto {
    const val service = "1.0-rc7"
    const val common = "0.11"
  }

  object Gradle {
    const val inCap = "0.2"
  }
}

object Deps {
  object KSP {
    const val classpath = "com.google.devtools.ksp:symbol-processing-gradle-plugin:${Versions.Kotlin.ksp}"
    const val api = "com.google.devtools.ksp:symbol-processing-api:${Versions.Kotlin.ksp}"
    const val impl = "com.google.devtools.ksp:symbol-processing:${Versions.Kotlin.ksp}"
  }

  object Square {
    const val kotlinPoet = "com.squareup:kotlinpoet:${Versions.Square.kotlinPoet}"
    const val javaPoet = "com.squareup:javapoet:${Versions.Square.javaPoet}"
  }

  object Android {
    const val gradle = "com.android.tools.build:gradle:${Versions.Android.gradle}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.Android.appcompat}"
    const val coreKtx = "androidx.core:core-ktx:${Versions.Android.coreKtx}"
  }

  object Testing {
    const val jUnit = "junit:junit:${Versions.Testing.jUnit}"
    const val mockK = "io.mockk:mockk:${Versions.Testing.mockK}"
    const val assertJ = "org.assertj:assertj-core:${Versions.Testing.assertJ}"

    object KotlinCompile {
      const val base = "com.github.tschuchortdev:kotlin-compile-testing:${Versions.Testing.kotlinCompile}"
      const val ksp = "com.github.tschuchortdev:kotlin-compile-testing-ksp:${Versions.Testing.kotlinCompile}"
    }
  }

  object GoogleAuto {
    const val serviceAnnotations = "com.google.auto.service:auto-service-annotations:${Versions.GoogleAuto.service}"
    const val service = "com.google.auto.service:auto-service:${Versions.GoogleAuto.service}"
    const val common = "com.google.auto:auto-common:${Versions.GoogleAuto.common}"
  }

  object AndroidProcessing {
    const val room = "androidx.room:room-compiler-processing:${Versions.AndroidProcessing.room}"
    const val roomTesting = "androidx.room:room-compiler-processing-testing:${Versions.AndroidProcessing.room}"
  }

  object Gradle {
    const val inCap = "net.ltgt.gradle.incap:incap:${Versions.Gradle.inCap}"
  }
}
