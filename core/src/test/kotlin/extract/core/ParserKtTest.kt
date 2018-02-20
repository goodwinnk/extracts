package extract.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ParserKtTest {
    @Test
    fun example() {
        val extracts = parseFile("src/test/resources/test.yaml").extracts
        assertEquals(5, extracts.size)

        assertEquals(
                Extract("YouTrack", "^.*(KT-\\d+).*$", null, arrayOf(),
                        icon = "path", text = "\${1}", hint = "\${1}", url = "https://youtrack.jetbrains.com/issue/\${1}"),
                extracts[0])

        assertEquals(
                Extract("IDE", null, null, arrayOf("idea/**")),
                extracts[1]
        )

        assertEquals(
                Extract("Minor", null, null, arrayOf(), style = "e1"),
                extracts[2]
        )

        assertEquals(
                Extract("WithBadge", null, null, arrayOf(), badge = "\${matched}"),
                extracts[3]
        )

        assertEquals(
                Extract("WithMessagePattern",
                        titlePattern = null, messagePattern = "^.*(KT-\\d+).*$",
                        files = arrayOf(), badge = null),
                extracts[4]
        )
    }
}