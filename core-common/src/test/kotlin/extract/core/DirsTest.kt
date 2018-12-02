package extract.core

import kotlin.test.Test
import kotlin.test.assertEquals

class DirsTest {
    @Test
    fun labelName() {
        assertEquals("Gradle Plugin Api", toLabelName("kotlin-gradle-plugin-api", withDrop("Kotlin")))
    }

    @Test
    fun simpleTerminate() {
        testLabels(
                "one/two/terminate/other/file.kt",
                withTerminate("terminate"),
                listOf("One|0", "Two|1")
        )
    }

    @Test
    fun prefixTerminate() {
        testLabels(
                "one/two/terminate/other/file.kt",
                withTerminate("one/two/terminate"),
                listOf("One|0", "Two|1")
        )
    }

    @Test
    fun upperCase() {
        testLabels(
                "one/one two one/some.kt",
                withUpper("one"),
                listOf("ONE|0", "ONE Two ONE|1")
        )
    }

    @Test
    fun removeRepeats() {
        testLabels(
                "one/one two/one/two/some.kt",
                withEmpty(),
                listOf("One|0", "One Two|1")
        )
    }

    private fun testLabels(path: String, dirsForEngine: DirsForEngine, labelStrings: List<String>) {
        testLabels(labelStrings, assignLabels(path, dirsForEngine))
    }

    private fun testLabels(labelStrings: List<String>, actualLabels: ArrayList<Pair<String, Int>>) {
        assertEquals(
                labelStrings.map {
                    val pair = it.split("|")
                    assertEquals(2, pair.size)
                    pair[0] to pair[1].toInt()
                },
                actualLabels
        )
    }

    private fun withEmpty(): DirsForEngine {
        return DirsForEngine(Dirs())
    }

    private fun withUpper(vararg values: String): DirsForEngine {
        @Suppress("UNCHECKED_CAST")
        return DirsForEngine(Dirs(upperCase = (values as Array<String>)))
    }

    private fun withTerminate(vararg values: String): DirsForEngine {
        @Suppress("UNCHECKED_CAST")
        return DirsForEngine(Dirs(terminate = (values as Array<String>)))
    }

    private fun withDrop(vararg values: String): DirsForEngine {
        @Suppress("UNCHECKED_CAST")
        return DirsForEngine(Dirs(drop = (values as Array<String>)))
    }
}