package extract.demo

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream


fun main(args: Array<String>) {
    val yaml = Yaml()

    val extracts = yaml.loadAs(FileInputStream(File("src/test/resources/example.yaml")), Extracts::class.java)

    extracts.forEach { println(it) }
}

data class Extract(val name: String)
class Extracts : Iterable<Extract> {
    var extracts: List<Extract>? = null

    override fun iterator() = extracts!!.iterator()
    override fun toString() = extracts.toString()
}
