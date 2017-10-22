package extract

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParserKtTest {
    @Test
    fun example() {
        val extracts = parseFile("src/test/resources/test.yaml").extracts
        assertEquals(4, extracts.size)

        assertEquals(
                Extract("YouTrack", "^.*(KT-\\d+).*$", listOf(),
                        icon = "path", text = "\${1}", hint = "\${1}", url = "https://youtrack.jetbrains.com/issue/\${1}"),
                extracts[0])

        assertEquals(
                Extract("IDE", null, listOf("idea/**")),
                extracts[1]
        )

        assertEquals(
                Extract("Minor", null, listOf(), style = "e1"),
                extracts[2]
        )

        assertEquals(
                Extract("WithBadge", null, listOf(), badge = "\${matched}"),
                extracts[3]
        )
    }
}