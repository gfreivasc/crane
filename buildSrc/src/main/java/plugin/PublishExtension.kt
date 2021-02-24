package plugin

open class PublishExtension {
    var groupId: String = DEFAULT_GROUP_ID
    var artifactId: String = ""
    var version: String = ""
    var versionSuffix: String = ""

    var publishComponents: Boolean = true

    internal var gitHub: GitHubPublishExtension = GitHubPublishExtension()
    internal var isGithubPackage: Boolean = false
    fun gitHub(config: GitHubPublishExtension.() -> Unit) {
        gitHub.config()
        isGithubPackage = true
    }

    companion object {
        const val DEFAULT_GROUP_ID = "com.gabrielfv"
    }

    open class GitHubPublishExtension {
        var repository: String = ""
        var username: String = ""
        var password: String = ""
    }
}