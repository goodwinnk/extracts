package extract

import extract.Extract
import extract.parseFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserKtTest {
    @Test
    fun example() {
        val extracts = parseFile("src/test/resources/test.yaml").extracts
        assertEquals(2, extracts.size)

        assertEquals(
                Extract("YouTrack", "^.*(KT-\\d+).*$", listOf(),
                        "path", "\${1}", "\${1}", "https://youtrack.jetbrains.com/issue/\${1}"),
                extracts[0])

        assertEquals(
                Extract("IDE", null, listOf("idea/**"), null, null, null, null),
                extracts[1]
        )
    }
}