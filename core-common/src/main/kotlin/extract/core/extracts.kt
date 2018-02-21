package extract.core

data class Extract(
        val name: String,

        val titlePattern: String? = null,
        val messagePattern: String? = null,
        val files: Array<String> = arrayOf(),

        val icon: String? = null,
        val text: String? = null,
        val hint: String? = null,
        val url: String? = null,
        val style: String? = null,
        val badge: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Extract

        if (name != other.name) return false
        if (titlePattern != other.titlePattern) return false
        if (messagePattern != other.messagePattern) return false
        if (!files.contentDeepEquals(other.files)) return false
        if (icon != other.icon) return false
        if (text != other.text) return false
        if (hint != other.hint) return false
        if (url != other.url) return false
        if (style != other.style) return false
        if (badge != other.badge) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + (titlePattern?.hashCode() ?: 0)
        result = 31 * result + (messagePattern?.hashCode() ?: 0)
        result = 31 * result + files.contentDeepHashCode()
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (hint?.hashCode() ?: 0)
        result = 31 * result + (url?.hashCode() ?: 0)
        result = 31 * result + (style?.hashCode() ?: 0)
        result = 31 * result + (badge?.hashCode() ?: 0)
        return result
    }
}

data class ExtractLabel(
        val name: String,
        val text: String?,
        val icon: String?,
        val hint: String?,
        val url: String?,
        val style: String?,
        val badges: Array<String>
)

interface PredefinedValues {
    val count: Int
    val matches: Int
}

object PredefinedVariables {
    const val COUNT = "count"
    const val MATCHES = "matches"
}

data class Extracts(val extracts: List<Extract>)

data class CommitInfo(
        val hash: String,
        val parentHashes: List<String>,

        val author: User,
        val committer: User,

        val time: Int,
        val title: String,
        val message: String,

        val fileActions: List<FileAction>
)

enum class Action {
    ADD,
    MODIFY,
    DELETE,
    RENAME,
    COPY;
}

data class FileAction(val action: Action, val path: String)
data class User(val name: String, val email: String)