package extract.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ParserKtTest {
    @Test
    fun example() {
        val extracts = parseFile("src/test/resources/test.yaml").extracts
        assertEquals(8, extracts.size)

        assertEquals(
                Extract("YouTrack", "^.*(KT-\\d+).*$", null, arrayOf(),
                        icon = "path", text = "\${1}", hint = "\${1}", url = "https://youtrack.jetbrains.com/issue/\${1}"),
                extracts[0])

        assertEquals(
                Extract("IDE", null, null, arrayOf("idea/**"), labelName = "IDE"),
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

        assertEquals(
                Extract("WithTrueLabel",
                        titlePattern = null, messagePattern = null,
                        files = arrayOf(), badge = null, labelName = "WithTrueLabel"),
                extracts[5]
        )

        assertEquals(
                Extract("WithWordsLabel",
                        titlePattern = null, messagePattern = null,
                        files = arrayOf(), badge = null, labelName = "With Words Label"),
                extracts[6]
        )

        assertEquals(
                Extract("WithFilesButNoLabel",
                        titlePattern = null, messagePattern = null,
                        files = arrayOf("idea/**"), badge = null, labelName = null),
                extracts[7]
        )
    }
}