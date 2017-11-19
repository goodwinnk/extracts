package extract.core

import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.nio.file.Files


class ReadCommitsWorkTreeTest {
    companion object {
        var tempDir: File? = null

        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            val testDir = Files.createTempDirectory(this::class.java.simpleName).toFile()
            Assert.assertTrue(testDir.isDirectory)

            tempDir = testDir

            val baseDir = File(BASE_PATH)
            baseDir.copyRecursively(testDir, true)

            renameWorkTreeRepo(testDir, true)
        }

        @JvmStatic
        @AfterClass
        fun afterClass() {
            tempDir?.deleteRecursively()
            tempDir = null
        }
    }

    val nk = User("Nikolay Krasko", "goodwinnk@gmail.com")

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

private const val BASE_PATH = "../core/src/test/testData/worktree/"
private const val TEMP_GIT = "temp-git"
private const val DOT_GIT = ".git"

private fun renameWorkTreeRepo(baseDir: File, toGit: Boolean) {
    val mainDir = File(baseDir, "main")
    val otherDir = File(baseDir, "other")

    val mainGitDir = File(mainDir, DOT_GIT)
    val otherDitFile = File(otherDir, DOT_GIT)
    if (toGit) {
        File(mainDir, TEMP_GIT).renameTo(mainGitDir)
        File(otherDir, TEMP_GIT).renameTo(otherDitFile)

        val otherInMainGitDirFile = File(mainGitDir, "worktrees/other/gitdir")

        val filesWithBaseDir = listOf(otherInMainGitDirFile, otherDitFile)
        for (file in filesWithBaseDir) {
            file.writeText(file.readText().replace("\$BASE_DIR", baseDir.canonicalPath))
        }
    } else {
        mainGitDir.renameTo(File(mainDir, TEMP_GIT))
        otherDitFile.renameTo(File(otherDir, TEMP_GIT))
    }
}