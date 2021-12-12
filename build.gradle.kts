buildscript {
  repositories {
    mavenCentral()
    google()
  }
  dependencies {
    classpath(libs.kotlin.gradle)
    classpath(libs.android.gradle)
    classpath(libs.ksp.gradle)
  }
}

tasks.register<Delete>("clean") {
  delete(rootProject.buildDir)
}

tasks.withType<Wrapper> {
  distributionType = Wrapper.DistributionType.ALL
}
