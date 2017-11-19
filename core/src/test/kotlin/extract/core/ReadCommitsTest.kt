package extract.core

import org.junit.Assert
import org.junit.Test

class ReadCommitsTest {
    val nk = User("Nikolay Krasko", "goodwinnk@gmail.com")

    @Test
    fun readFirstCommit() {
        checkCommit(
                CommitInfo(
                        hash = "4e028c4d4c6cb4bd292d07a950056a8f568e66c6",
                        parentHashes = listOf(),
                        author = nk,
                        committer = nk,
                        time = 1506291116,
                        title = "Init commit",
                        message = "Init commit\n",
                        fileActions = listOf(
                                FileAction(Action.ADD, "build.gradle"),
                                FileAction(Action.ADD, "gradle/wrapper/gradle-wrapper.jar"),
                                FileAction(Action.ADD, "gradle/wrapper/gradle-wrapper.properties"),
                                FileAction(Action.ADD, "gradlew"),
                                FileAction(Action.ADD, "gradlew.bat"),
                                FileAction(Action.ADD, "settings.gradle"),
                                FileAction(Action.ADD, "src/main/kotlin/extract/demo/main.kt"),
                                FileAction(Action.ADD, "src/main/kotlin/extract/demo/parser.kt"),
                                FileAction(Action.ADD, "src/test/resources/example.yaml")
                        ))
        )
    }

    @Test
    fun readMiddleCommit() {
        checkCommit(
                CommitInfo(
                        "e720f557c51d957dd3c5cdb3ecbf83cb9a916a83",
                        parentHashes = listOf("469f3104cb7762e8a2202aab8824ebeea0763c98"),
                        author = nk,
                        committer = nk,
                        time = 1506670977,
                        title = "Parse example file with Jackson",
                        message = "Parse example file with Jackson\n",
                        fileActions = listOf(
                                FileAction(Action.MODIFY, path = "build.gradle"),
                                FileAction(Action.MODIFY, path = "src/main/kotlin/extract/demo/parser.kt"),
                                FileAction(Action.MODIFY, path = "src/test/resources/example.yaml")
                        )))
    }

    @Test
    fun readSeveralCommits() {
        val commits = readCommits(OWN_GIT_PATH, "a6a653763b676c0a88ac07039a734d3add845cbd", 3)
        Assert.assertEquals(3, commits.size)

        val first = commits.first()
        Assert.assertEquals("a6a653763b676c0a88ac07039a734d3add845cbd", first.hash)
        Assert.assertEquals("Abstract commit information", first.title)

        val last = commits.last()
        Assert.assertEquals("e720f557c51d957dd3c5cdb3ecbf83cb9a916a83", last.hash)
        Assert.assertEquals("Parse example file with Jackson", last.title)
    }

    @Test
    fun readTooManyCommits() {
        val commits = readCommits(OWN_GIT_PATH, "a6a653763b676c0a88ac07039a734d3add845cbd", 1000)
        Assert.assertEquals(5, commits.size)
    }

    @Test
    fun readBranch() {
        val commits = readCommits(OWN_GIT_PATH, "refs/heads/master", 1)
        Assert.assertEquals(1, commits.size)
    }

    private fun checkCommit(expected: CommitInfo) {
        val commits = readCommits(OWN_GIT_PATH, expected.hash, 1)
        val commit = commits.single()

        Assert.assertEquals(expected, commit)
    }
}

private val OWN_GIT_PATH = "../.git"