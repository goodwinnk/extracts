package extract.core

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File


fun parseFile(path: String): Extracts {
    val mapper = ObjectMapper(YAMLFactory())
    val extractsInternal = mapper.readValue(File(path), ExtractsInternal::class.java)

    return extractsInternal.toExtracts()
}

private class ExtractInternal {
    var name: String? = null

    @JsonProperty("title-pattern")
    var titlePattern: String? = null
    @JsonProperty("message-pattern")
    var messagePattern: String? = null
    var files: Array<String> = arrayOf()

    var text: String? = null
    var icon: String? = null
    var hint: String? = null
    var url: String? = null
    var style: String? = null
    var badge: String? = null
    var label: Boolean? = null

    @JsonProperty("label-name")
    var labelName: String? = null

    override fun toString() =
            "$name, $titlePattern, $messagePattern, $files, $icon, $text, $hint, $url, $style, $badge"

    fun toExtract(): Extract {
        if (name == null) {
            throw IllegalStateException("Extract doesn't have name: $this")
        }

        return Extract(
                name!!,
                titlePattern = titlePattern,
                messagePattern = messagePattern,
                files = files,
                icon = icon,
                text = text,
                hint = hint ?: text,
                url = url,
                style = style,
                badge = badge,
                labelName = if (label == true || labelName != null) (labelName ?: name) else null)
    }
}

private class ExtractsInternal : Iterable<ExtractInternal> {
    var extracts: List<ExtractInternal>? = null

    override fun iterator() = extracts!!.iterator()
    override fun toString() = extracts.toString()

    fun toExtracts(): Extracts {
        if (extracts == null) {
            return Extracts(listOf())
        }
        return Extracts(extracts!!.map { it.toExtract() })
    }
}
