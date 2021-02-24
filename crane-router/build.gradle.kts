plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
    id("internal.publishing")
}

group = "com.gabrielfv"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.30-dev-experimental-20210205")
    implementation("com.google.auto.service:auto-service-annotations:1.0-rc7")
    compileOnly("com.google.devtools.ksp:symbol-processing:1.4.30-dev-experimental-20210205")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.3.5")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.3.5")
    testImplementation("com.google.devtools.ksp:symbol-processing:1.4.30-dev-experimental-20210205")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.19.0")
    ksp("dev.zacsweers.autoservice:auto-service-ksp:0.3.2")
}

internalPublishing {
    artifactId = "crane-router"
    groupId = findProperty("library.groupId") as String
    version = findProperty("library.version") as String
    versionSuffix = System.getenv("VERSION_SUFFIX").orEmpty()

    gitHub {
        repository = findProperty("library.repository") as String
        username = System.getenv("GITHUB_PUBLISH_USERNAME").orEmpty()
        password = System.getenv("GITHUB_PUBLISH_TOKEN").orEmpty()
    }
}
