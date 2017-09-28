package extract.demo

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import java.io.File


fun main(args: Array<String>) {
    val mapper = ObjectMapper(YAMLFactory())
    val extracts = mapper.readValue(File("src/test/resources/example.yaml"), Extracts::class.java)

    println(extracts)
}

private class Extract {
    var name: String? = null

    @JsonProperty("title-pattern")
    var titlePattern: String? = null
    var files: List<String> = listOf()

    var icon: String? = null
    var hint: String? = null
    var url: String? = null

    override fun toString(): String {
        return "$name, $titlePattern, $files, $icon, $hint, $url"
    }
}

private class Extracts : Iterable<Extract> {
    var extracts: List<Extract>? = null

    override fun iterator() = extracts!!.iterator()
    override fun toString() = extracts.toString()
}
