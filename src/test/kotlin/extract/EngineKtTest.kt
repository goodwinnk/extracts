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
                        icon = "path", url = "https://youtrack.jetbrains.com/issue/KT-20135"),
                label
        )

    }

    @Test
    fun fileMatchTest() {
        Assertions.assertTrue(pathMatch(".idea/some", listOf(".idea/**")))
        Assertions.assertTrue(pathMatch("Changes.md", listOf("Changes.md")))
    }
}

val DUMMY_USER = User("dummyUserName", "dummyUserEmail")
val DUMMY_ACTION = FileAction(Action.ADD, "dummyFileActionPath")

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