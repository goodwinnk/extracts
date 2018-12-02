package extract.core

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File


fun parseFile(path: String): ExtractsConfig {
    val mapper = ObjectMapper(YAMLFactory())
    val extractsInternal = mapper.readValue(File(path), ExtractsConfigInternal::class.java)

    return extractsInternal.toExtractsConfig()
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
                labelName = if ((label != false) && (label == true || labelName != null || !files.isEmpty()))
                    (labelName ?: name)
                else
                    null
        )
    }
}

private class ExtractsConfigInternal {
    var extracts: List<ExtractInternal>? = null
    var dirs: DirsConfig? = null

    private fun getExtracts(): Extracts {
        if (extracts == null) {
            return Extracts(listOf())
        }
        return Extracts(extracts!!.map { it.toExtract() })
    }

    private fun getDirsConfig(): Dirs {
        if (dirs == null) {
            return Dirs()
        }

        return dirs!!.toDirs()
    }

    fun toExtractsConfig(): ExtractsConfig {
        return ExtractsConfig(getExtracts(), getDirsConfig())
    }
}

private class DirsConfig {
    var skip: Array<String>? = null
    var drop: Array<String>? = null
    var terminate: Array<String>? = null
    var rename: Array<String>? = null

    @JsonProperty("upper-case")
    var upperCase: Array<String>? = null

    fun toDirs(): Dirs {
        return Dirs(
                skip = skip ?: arrayOf(),
                drop = drop ?: arrayOf(),
                terminate = terminate ?: arrayOf(),
                rename = rename ?: arrayOf(),
                upperCase = upperCase ?: arrayOf()
        )
    }
}