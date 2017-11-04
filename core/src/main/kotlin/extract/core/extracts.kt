package extract.core

data class Extract(
        val name: String,

        val titlePattern: String? = null,
        val files: List<String> = listOf(),

        val icon: String? = null,
        val text: String? = null,
        val hint: String? = null,
        val url: String? = null,
        val style: String? = null,
        val badge: String? = null
)

data class ExtractLabel(
        val name: String,
        val text: String?,
        val icon: String?,
        val hint: String?,
        val url: String?,
        val style: String?,
        val badges: List<String>
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

const val EMPTY_HASH = "EMPTY_HASH"

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