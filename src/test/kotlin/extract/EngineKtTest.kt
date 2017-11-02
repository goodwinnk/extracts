package extract

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class EngineKtTest {
    @Test
    fun assignLabelWithPatterns() {
        val label = assignLabel(
                testCommit(title = "Open created actual class in editor #KT-20135 Fixed"),
                Extract("YouTrack", "^.*(KT-\\d+).*$", listOf(),
                        "path", "\${1}", "\${0}", "https://youtrack.jetbrains.com/issue/\${1}")
        )

        Assertions.assertEquals(
                ExtractLabel(
                        "YouTrack", "KT-20135",
                        hint = "Open created actual class in editor #KT-20135 Fixed",
                        icon = "path",
                        url = "https://youtrack.jetbrains.com/issue/KT-20135",
                        style = null,
                        badges = listOf()),
                label
        )

    }

    @Test
    fun fileMatchTest() {
        Assertions.assertTrue(pathMatch(".idea/some", listOf(".idea/**")))
        Assertions.assertTrue(pathMatch("Changes.md", listOf("Changes.md")))
        Assertions.assertTrue(pathMatch("one/two/ui/three", listOf("**/ui/**")))
    }

    @Test
    fun matchesVariable() {
        val label = assignLabel(
                testCommit(
                        title = "Some",
                        fileActions = listOf(
                                FileAction(Action.ADD, "first.test"),
                                FileAction(Action.MODIFY, "second.test"),
                                FileAction(Action.RENAME, "second.other")
                        )),
                Extract("YouTrack",
                        titlePattern = null, files = listOf("**.test"),
                        icon = "path",
                        text = "\${matches}\\\${count}",
                        hint = "\${matches}\\\${count}",
                        url = "\${matches}\\\${count}",
                        badge = "\${matches}\\\${count}")
        )

        Assertions.assertEquals(
                ExtractLabel(
                        "YouTrack",
                        text = "2\\3",
                        hint = "2\\3",
                        icon = "path",
                        url = "2\\3",
                        style = null,
                        badges = listOf("2\\3")),
                label
        )
    }
}

private val DUMMY_USER = User("dummyUserName", "dummyUserEmail")

fun testCommit(
        hash: String = "dummyHash",
        parentHashes: List<String> = listOf(),
        author: User = DUMMY_USER,
        committer: User = DUMMY_USER,
        time: Int = 0,
        title: String = "dummyTitle",
        message: String = "dummyMessage",
        fileActions: List<FileAction> = listOf()
        ) = CommitInfo(hash, parentHashes, author, committer, time, title, message, fileActions)