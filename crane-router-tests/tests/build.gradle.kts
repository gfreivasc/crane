plugins {
  alias(libs.plugins.kotlin.jvm)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "17"
  }
}

kotlin {
  kotlinDaemonJvmArgs = listOf(
    "-Dfile.encoding=UTF-8",
    "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
  )
}

dependencies {
  implementation(projects.craneRouter)
  implementation(projects.craneAnnotations)
  implementation(kotlin("stdlib"))
  implementation(libs.ksp.api)
  implementation(libs.google.auto.common)
  implementation(libs.androidx.room.processing)
  implementation(libs.test.kotlin.compile.core)
  implementation(libs.test.kotlin.compile.ksp)
  implementation(libs.junit)
  implementation(libs.assertj)

  testImplementation(projects.craneRouterTests.fakeAndroid)
  testImplementation(projects.craneRouterTests.dummyModuleA)
  testImplementation(projects.craneRouterTests.dummyModuleB)
  testImplementation(libs.ksp.impl)
}
