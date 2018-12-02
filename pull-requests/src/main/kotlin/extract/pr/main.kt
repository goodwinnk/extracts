package extract.pr

import extract.core.*
import org.kohsuke.github.*

fun main(args: Array<String>) {
    // Inspections -> IDE
    // Completion? 1952, 1951 -> IDE
    // Resolve? 1959 -> Compiler
    // IDL2K -> Build 1950

    val extracts = parseFile("demo/src/main/resources/kotlin.yaml")
    val labeledExtracts = Extracts(extracts.extracts.extracts.filter { it.labelName != null })

    val connect = GitHub.connect()
    val repository = connect.getRepository("JetBrains/kotlin")


    val skip: Set<Int> = setOf(1021, 1182)
    val prs = repository.queryPullRequests()
            .state(GHIssueState.OPEN)
            .list().iterator().asSequence()
//            .filter { it.number == 1938 }
            .filter { it.number !in skip }
            .filter { it.labels.isEmpty() }


    val finalLabels = setOf(
            "Inspections",
            "IDEA",
            "Completion",
            "Backend"
//            "J2K"
//            "Compiler",
    )


    val map = mapOf<String, String>(
            "Quickfix" to "Inspections",
            "Intentions" to "Inspections"
    )

    val remove = mapOf<String, Set<String>>(
            "Inspections" to setOf("IDEA")
    )

    for (pr in prs) {
        val labels = getLabels(labeledExtracts, pr.fetchCommitInfos())
                .map {
                    val newName = map[it.name]
                    if (newName != null) {
                        it.copy(name = newName)
                    } else {
                        it
                    }
                }

        val labelNames = labels.map { it.name }

        val expectedLabels: Set<String> = labelNames.let {
            val reducedLabelNamesSet = it.toMutableSet()
            for ((labelName, toRemove) in remove) {
                if (labelNames.contains(labelName)) {
                    reducedLabelNamesSet.removeAll(toRemove)
                }
            }

            reducedLabelNamesSet
        }

        val existingLabels = pr.labels.map { it.name }

        val newLabels = expectedLabels - existingLabels

        println("${pr.htmlUrl}: ${pr.title}")
        println("  Existing: $existingLabels" )
        println("  New: $newLabels")

        if (!newLabels.isEmpty()) {

            val assignLabels = newLabels.filter { it in finalLabels }
            if (!assignLabels.isEmpty()) {

                println("  Assign: $assignLabels")
//                pr.setLabels(*(assignLabels + existingLabels).toTypedArray())
            }
        }

        println()
    }
}

private fun getLabels(labeledExtracts: Extracts, commitInfos: List<CommitInfo>): LinkedHashSet<ExtractLabel> {
    val labels = LinkedHashSet<ExtractLabel>()

    for (commitInfo in commitInfos) {
        val commitLabels = assignLabels(commitInfo, labeledExtracts)
        labels.addAll(commitLabels)
    }

    return labels
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

    listCommits()
            .asSequence()
            .map { commitDetails ->
                repository.getCommit(commitDetails.sha)
            }
            .filter { commit ->
                commit.parents.size <= 1 // Filter out merge commits
            }
            .mapTo(result) { commit ->
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
