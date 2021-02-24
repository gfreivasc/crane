package plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.register
import java.net.URI

private const val EXTENSION_NAME = "internalPublishing"
private const val ANDROID_SOURCES_JAR = "androidSourcesJar"

class Publishing : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("maven-publish")

        val extension = target.extensions.create<PublishExtension>(EXTENSION_NAME)

        // There should be a config for non-android projects as well
        val androidExt = target.extensions.findByType<BaseExtension>()
        val jarTask = androidExt?.let { android ->
            target.tasks.register<Jar>(ANDROID_SOURCES_JAR) {
                archiveClassifier.set("sources")
                from(android.sourceSets.getByName("main").java.srcDirs)
            }
        }

        target.afterEvaluate { project ->
            (project as ExtensionAware).extensions
                .findByType<PublishingExtension>()
                ?.applyPublishing(extension, project.publishComponent, jarTask?.get())
        }
    }

    private fun PublishingExtension.applyPublishing(
        extension: PublishExtension,
        component: SoftwareComponent,
        androidJarTask: Jar? = null
    ) {
        if (extension.publishComponents) {
            publications { publication ->
                publication.create<MavenPublication>("maven") {
                    from(component)

                    groupId = extension.groupId
                    artifactId = extension.artifactId
                    version = if (extension.versionSuffix.isNotBlank()) {
                        "${extension.version}-${extension.versionSuffix}"
                    } else {
                        extension.version
                    }

                    if (androidJarTask != null) {
                        artifact(androidJarTask)
                    }
                }
            }
        }

        repositories { repos ->
            if (extension.isGithubPackage) {
                repos.maven { maven ->
                    maven.name = "GithubPackages"
                    maven.url = URI.create(
                        "https://maven.pkg.github.com/gfreivasc/${extension.gitHub.repository}"
                    )
                    maven.credentials { credentials ->
                        credentials.username = extension.gitHub.username
                        credentials.password = extension.gitHub.password
                    }
                }
            }
        }
    }

    private val Project.publishComponent: SoftwareComponent get() {
        val android = components.findByName("release")
        val jvm = components.findByName("java")
        return android ?: jvm
        ?: throw IllegalStateException(
            """
                Required components for publication not found. Make sure to apply [internalPublish]
                plugin *after* your android/kotlin plugin that creates the required components.
                """.trimIndent().replace('\n', ' ')
        )
    }
}
