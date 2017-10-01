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