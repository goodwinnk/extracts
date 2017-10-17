package extract

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
    var files: List<String> = listOf()

    var text: String? = null
    var icon: String? = null
    var hint: String? = null
    var url: String? = null
    var style: String? = null

    override fun toString(): String {
        return "$name, $titlePattern, $files, $icon, $text, $hint, $url, $style"
    }

    fun toExtract(): Extract {
        if (name == null) {
            throw IllegalStateException("Extract doesn't have name: $this")
        }

        return Extract(name!!, titlePattern, files,
                icon = icon,
                text = text,
                hint = hint ?: text,
                url = url,
                style = style)
    }
}

private class ExtractsInternal : Iterable<ExtractInternal> {
    var extracts: List<ExtractInternal>? = null

    override fun iterator() = extracts!!.iterator()
    override fun toString() = extracts.toString()

    fun toExtracts(): Extracts {
        if (extracts == null) {
            throw IllegalStateException("Extracts wasn't parsed properly: $this")
        }
        return Extracts(extracts!!.map { it.toExtract() })
    }
}
