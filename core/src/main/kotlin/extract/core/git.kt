package extract.core

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import java.io.File

fun readCommits(repositoryPath: String, revisionString: String, numberOfCommits: Int): List<CommitInfo> {
    val repository = FileRepository(File(repositoryPath))
    Git(repository).use { git ->
        val branch = repository.resolve(revisionString)

        return git.log().add(branch).setMaxCount(numberOfCommits).call().map { commit ->
            with(commit) {
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
        }
    }
}

fun PersonIdent.toUser() = User(name, emailAddress)

fun collectActions(git: Git, commit: RevCommit): List<FileAction> {
    val reader = git.repository.newObjectReader()

    val oldTreeIterator =
            if (commit.parentCount != 0) {
                CanonicalTreeParser().apply {
                    reset(reader, commit.getParent(0).tree)
                }

            } else {
                EmptyTreeIterator()
            }

    val newTreeIterator = CanonicalTreeParser().apply {
        reset(reader, commit.tree)
    }

    val diffCommand = git.diff().apply {
        setOldTree(oldTreeIterator)
        setNewTree(newTreeIterator)
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