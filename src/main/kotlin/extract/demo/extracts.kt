package extract.demo

data class Extract(
    val name: String,
    val titlePattern: String? = null,
    val files: List<String> = listOf(),

    val icon: String?,
    val hint: String?,
    val url: String?
)

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