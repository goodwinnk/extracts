package extract.core

import org.junit.Assert
import org.junit.Test

internal class EngineKtTest {
    @Test
    fun assignLabelWithPatterns() {
        val label = assignLabel(
                testCommit(title = "Open created actual class in editor #KT-20135 Fixed"),
                Extract("YouTrack", "^.*(KT-\\d+).*$", null, listOf(),
                        "path", "\${1}", "\${0}", "https://youtrack.jetbrains.com/issue/\${1}")
        )

        Assert.assertEquals(
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
    fun assignLabelWithMessagePattern() {
        val title = "Open created actual class in editor"
        val label = assignLabel(
                testCommit(hash = "123", title = title, message = "$title\n\n #KT-20135 Fixed"),
                Extract("YouTrack", null, "^.*(KT-\\d+).*$", listOf(),
                        "path", "\${1}", "\${1}", "https://youtrack.jetbrains.com/issue/\${1}")
        )

        Assert.assertEquals(
                ExtractLabel(
                        "YouTrack", "KT-20135",
                        hint = "KT-20135",
                        icon = "path",
                        url = "https://youtrack.jetbrains.com/issue/KT-20135",
                        style = null,
                        badges = listOf()),
                label
        )
    }

    @Test
    fun assignLabelByBothPatterns() {
        val extract =
                Extract("Some",
                        titlePattern = "^.*(first).*$",
                        messagePattern = "^.*(second).*$",
                        text = "\${1}")

        Assert.assertEquals(
                ExtractLabel(name = "Some", text = "first", icon = null, hint = null, url = null, style = null, badges = listOf()),
                assignLabel(
                        testCommit(hash = "123", title = "bla bla bla first", message = "bla bla bla first\n\nfoo foo foo first second"),
                        extract)
        )

        Assert.assertEquals(
                ExtractLabel(name = "Some", text = "second", icon = null, hint = null, url = null, style = null, badges = listOf()),
                assignLabel(
                        testCommit(hash = "345", title = "bla bla bla", message = "bla bla bla\n\nfoo foo foo first second"),
                        extract)
        )
    }

    @Test
    fun fileMatchTest() {
        Assert.assertTrue(pathMatch(".idea/some", listOf(".idea/**")))
        Assert.assertTrue(pathMatch("Changes.md", listOf("Changes.md")))
        Assert.assertTrue(pathMatch("one/two/ui/three", listOf("**/ui/**")))
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

        Assert.assertEquals(
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

private val dummyUser = User("dummyUserName", "dummyUserEmail")

fun testCommit(
        hash: String = "dummyHash",
        parentHashes: List<String> = listOf(),
        author: User = dummyUser,
        committer: User = dummyUser,
        time: Int = 0,
        title: String = "dummyTitle",
        message: String = "dummyMessage",
        fileActions: List<FileAction> = listOf()
        ) = CommitInfo(hash, parentHashes, author, committer, time, title, message, fileActions)