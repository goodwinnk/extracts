package extract.demo

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ParserKtTest {
    @Test
    fun example() {
        val extracts = parseFile("src/test/resources/example.yaml").extracts
        assertEquals(5, extracts.size)

        assertEquals(
                Extract("YouTrack", "^.*KT-(\\d+).*$", listOf(), "path", "\${0}", "https://youtrack.jetbrains.com/issue/\${0}"),
                extracts[0])

        assertEquals(
                Extract("IDE", null, listOf("idea/**"), null, null, null),
                extracts[2]
        )
    }
}