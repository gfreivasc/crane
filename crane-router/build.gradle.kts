plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
    id("internal.publishing")
}

group = "com.gabrielfv"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(Deps.KSP.api)
    implementation(Deps.AutoService.annotations)
    compileOnly(Deps.KSP.impl)
    ksp(Deps.AutoService.ksp)

    testImplementation(Deps.Testing.KotlinCompile.base)
    testImplementation(Deps.Testing.KotlinCompile.ksp)
    testImplementation(Deps.KSP.impl)
    testImplementation(Deps.Testing.jUnit)
    testImplementation(Deps.Testing.assertJ)
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
