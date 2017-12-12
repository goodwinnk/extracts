package extract.core

import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File

class ReadCommitsWorkTreeTest {
    companion object : TestDataGitInitializer() {
        override val testClassName: String = ReadCommitsWorkTreeTest::class.java.simpleName
        override val basePath = "../core/src/test/testData/worktree/"
        override val workTreeDirNames = listOf("other")

        @JvmStatic
        @BeforeClass
        override fun beforeClass() {
            super.beforeClass()
        }

        @JvmStatic
        @AfterClass
        override fun afterClass() {
            super.afterClass()
        }
    }

    private val nk = User("Nikolay Krasko", "goodwinnk@gmail.com")

    @Test
    fun readInMain() {
        checkCommit(
                "main",
                CommitInfo(
                        hash = "13c4d0200df99431abb24f7a88370f1dafaaad3c",
                        parentHashes = listOf("05f5173943f1244dbf807b4311e268d14a0cd76c"),
                        author = nk,
                        committer = nk,
                        time = 1511121311,
                        title = "Main only",
                        message = "Main only\n",
                        fileActions = listOf(FileAction(action = Action.MODIFY, path = "test.txt"))
                )
        )
    }

    @Test
    fun readInWorktree() {
        checkCommit(
                "other",
                CommitInfo(
                        "25d390be9f6fb95e15bc0dbed7c492c63d25ec43",
                        parentHashes = listOf("05f5173943f1244dbf807b4311e268d14a0cd76c"),
                        author = nk,
                        committer = nk,
                        time = 1511127701,
                        title = "Other only",
                        message = "Other only\n",
                        fileActions = listOf(FileAction(action = Action.MODIFY, path = "test.txt"))))
    }

    private fun checkCommit(workTreeSubDir: String, expected: CommitInfo) {
        val gitDir = File(tempDir!!, workTreeSubDir)
        Assert.assertTrue(gitDir.isDirectory)

        val commits = readCommits(gitDir.absolutePath, null, 1)
        val commit = commits.single()

        Assert.assertEquals(expected, commit)
    }
}