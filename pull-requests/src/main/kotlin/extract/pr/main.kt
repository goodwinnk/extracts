package extract.pr

import extract.core.*
import org.kohsuke.github.*

fun main(args: Array<String>) {
    val extracts = parseFile("demo/src/main/resources/kotlin.yaml")

    val connect = GitHub.connect()
    val repository = connect.getRepository("JetBrains/kotlin")

    val prs = repository.queryPullRequests()
            .state(GHIssueState.OPEN)
            .list().iterator().asSequence()
            .take(50)

    for (pr in prs) {
        val labels = LinkedHashSet<ExtractLabel>()

        val commitInfos = pr.fetchCommitInfos()
        for (commitInfo in commitInfos) {
            val commitLabels = assignLabels(commitInfo, extracts)
            labels.addAll(commitLabels.filter { it.name != "YouTrack" })
        }

        println("${pr.htmlUrl}: ${pr.title}")
        println("  " + labels.joinToString { it.name })
        println()
    }
}

private fun GHPullRequest.fetchCommitInfos(): List<CommitInfo> {
    val result = ArrayList<CommitInfo>()

    val titleInfo = CommitInfo(
            hash = "-- ${id} --",
            parentHashes = emptyArray(),
            author = user.toUser(),
            committer = user.toUser(),
            time = createdAt.time.toInt(),
            title = title,
            message = body,
            fileActions = emptyArray()
    )

    result.add(titleInfo)

    listCommits().asList().mapTo(result) { commitDetails ->
        val commit = repository.getCommit(commitDetails.sha)
        commit.toCommitInfo()
    }

    return result
}

private fun GHUser.toUser() = User(this.name ?: "", this.email ?: "")

private fun GHCommit.toCommitInfo(): CommitInfo {
    return CommitInfo(
            hash = shA1,
            parentHashes = parents.map { it.shA1 }.toTypedArray(),
            author = author?.toUser() ?: User("unknown", "un@known"),
            committer = committer?.toUser() ?: User("unknown", "un@known"),
            time = 0,
            title = commitShortInfo.message.substringBefore("\n"),
            message = commitShortInfo.message,
            fileActions = files.map { file ->
                file.toFileAction()
            }.toTypedArray()
    )
}

private fun GHCommit.File.toFileAction(): FileAction {
    val action = when (status) {
        "modified" -> Action.MODIFY
        "added" -> Action.ADD
        "removed" -> Action.DELETE
        "renamed" -> Action.RENAME
        else -> throw IllegalStateException("Unknown status: $status")
    }

    return FileAction(action, fileName)
}
