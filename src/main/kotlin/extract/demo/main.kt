package extract.demo

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import java.io.File


fun main(args: Array<String>) {
    val repository = FileRepository(File("C:/Projects/kotlin/.git"))
    Git(repository).use { git ->
        val masterBranch = repository.resolve("refs/heads/master")

        for (commit in git.log().add(masterBranch).setMaxCount(50).call()) {
            val commitInfo = with(commit) {
                CommitInfo(
                        hash = ObjectId.toString(id),
                        parentHashes = parents.map { ObjectId.toString(it) },
                        author = authorIdent.toUser(),
                        committer = committerIdent.toUser(),
                        time = commitTime,
                        title = shortMessage,
                        message = fullMessage,
                        fileActions = collectActions(git, commit)
                )
            }

            println(commitInfo)
        }
    }
}

fun PersonIdent.toUser() = User(name, emailAddress)

fun collectActions(git: Git, commit: RevCommit): List<FileAction> {
    val reader = git.repository.newObjectReader()

    val oldTreeIter = CanonicalTreeParser().apply {
        reset(reader, commit.getParent(0).tree)
    }

    val newTreeIter = CanonicalTreeParser().apply {
        reset(reader, commit.tree)
    }

    val diffCommand = git.diff().apply {
        setOldTree(oldTreeIter)
        setNewTree(newTreeIter)
    }

    return diffCommand.call().map { entry ->
        FileAction(entry.changeType.toAction(), entry.newPath)
    }
}

private fun DiffEntry.ChangeType.toAction() =
        when (this) {
            DiffEntry.ChangeType.ADD -> Action.ADD
            DiffEntry.ChangeType.MODIFY -> Action.MODIFY
            DiffEntry.ChangeType.DELETE -> Action.DELETE
            DiffEntry.ChangeType.RENAME -> Action.RENAME
            DiffEntry.ChangeType.COPY -> Action.COPY
        }