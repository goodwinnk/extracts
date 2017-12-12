package extract.core

import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

class ReadCommitsTest {
    companion object : TestDataGitInitializer() {
        override val testClassName = ReadCommitsTest::class.java.simpleName
        override val basePath = "../core/src/test/testData/readCommits/"

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

    val nk = User("Nikolay Krasko", "goodwinnk@gmail.com")

    @Test
    fun readFirstCommit() {
        checkCommit(
                CommitInfo(
                        hash = "eb3d607a078307b096d5c7c479b48322bfa2e967",
                        parentHashes = listOf(),
                        author = nk,
                        committer = nk,
                        time = 1513116076,
                        title = "init",
                        message = "init\n",
                        fileActions = listOf(
                                FileAction(Action.ADD, "first.txt"),
                                FileAction(Action.ADD, "second.txt"),
                                FileAction(Action.ADD, "third.txt")
                        )),
                mainGitPath()
        )
    }

    @Test
    fun readMiddleDeleteCommit() {
        checkCommit(
                CommitInfo(
                        "1ec85e17cc5c4537c1bd0f00730035a3c9a924dc",
                        parentHashes = listOf("eb3d607a078307b096d5c7c479b48322bfa2e967"),
                        author = nk,
                        committer = nk,
                        time = 1513116128,
                        title = "Delete third",
                        message = "Delete third\n",
                        fileActions = listOf(
                                FileAction(action=Action.DELETE, path="/dev/null")
                        )),
                mainGitPath()
        )
    }

    @Test
    fun readMiddleModifyCommit() {
        checkCommit(
                CommitInfo(
                        "090661dac86a607de00292592513214c8760c348",
                        parentHashes = listOf("1ec85e17cc5c4537c1bd0f00730035a3c9a924dc"),
                        author = nk,
                        committer = nk,
                        time = 1513116175,
                        title = "Modify first",
                        message = "Modify first\n\none -> one one\n",
                        fileActions = listOf(
                                FileAction(action = Action.MODIFY, path = "first.txt")
                        )),
                mainGitPath()
        )
    }

    @Test
    fun readSeveralCommits() {
        val commits = readCommits(mainGitPath(), "ec18450180ceceea54ad1a614bcdba2932aad032", 3)
        Assert.assertEquals(3, commits.size)

        val first = commits.first()
        Assert.assertEquals("ec18450180ceceea54ad1a614bcdba2932aad032", first.hash)
        Assert.assertEquals("Rename second -> third", first.title)

        val last = commits.last()
        Assert.assertEquals("1ec85e17cc5c4537c1bd0f00730035a3c9a924dc", last.hash)
        Assert.assertEquals("Delete third", last.title)
    }

    @Test
    fun readTooManyCommits() {
        val commits = readCommits(mainGitPath(), "ec18450180ceceea54ad1a614bcdba2932aad032", 1000)
        Assert.assertEquals(4, commits.size)
    }

    @Test
    fun readBranch() {
        val commits = readCommits(mainGitPath(), "refs/heads/master", 1)
        Assert.assertEquals(1, commits.size)
    }

    private fun checkCommit(expected: CommitInfo, gitPath: String) {
        val commits = readCommits(gitPath, expected.hash, 1)
        val commit = commits.single()

        Assert.assertEquals(expected, commit)
    }
}