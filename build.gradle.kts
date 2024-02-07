plugins {
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.kapt) apply false
  alias(libs.plugins.kotlin.parcelize) apply false
  alias(libs.plugins.android.app) apply false
  alias(libs.plugins.android.lib) apply false
  alias(libs.plugins.ksp) apply false
}

tasks.register<Delete>("clean") {
  delete(rootProject.layout.buildDirectory)
}

tasks.withType<Wrapper> {
  distributionType = Wrapper.DistributionType.ALL
}
