package extract.core

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import java.io.File
import java.io.IOException

class ConfigureGitException(path: String, cause: Throwable? = null):
        Throwable("Couldn't configure git at ${File(path).absolutePath}", cause)

private val COMMON_DIR_FILE_NAME = "commondir"

private fun configureRepository(path: String): Repository {
    val pathFile = File(path)
    val gitFile = if (pathFile.name == Constants.DOT_GIT || pathFile.name.endsWith(Constants.DOT_GIT)) {
        pathFile
    } else {
        File(pathFile.absolutePath, Constants.DOT_GIT)
    }

    if (!gitFile.exists()) {
        throw ConfigureGitException(path)
    }

    val isWorkTree = gitFile.isFile
    if (!isWorkTree) {
        return FileRepositoryBuilder.create(gitFile)
    }

    class PatchedFileRepositoryBuilder : FileRepositoryBuilder() {
        override fun setWorkTree(workTree: File?): PatchedFileRepositoryBuilder {
            super.setWorkTree(workTree)
            return this
        }

        fun setGitDirFromWorkTree(): PatchedFileRepositoryBuilder {
            setupGitDir()

            try {
                val gitCommonDirRelativePath = File(gitDir, COMMON_DIR_FILE_NAME).readText().trim()
                val gitCommonDirFile = File(gitDir, gitCommonDirRelativePath)

                gitDir = gitCommonDirFile
            } catch (io: IOException) {
                throw ConfigureGitException(path, io)
            }

            return this
        }
    }

    return PatchedFileRepositoryBuilder()
            .setWorkTree(gitFile.parentFile)
            .setGitDirFromWorkTree()
            .build()
}

private val Repository.fullBranchWithWorkTree: String get() {
    if (isBare) {
        return fullBranch
    }

    if (workTree == null) {
        return fullBranch
    }

    return FileRepositoryBuilder().setWorkTree(workTree).build().use { repository ->
        repository.fullBranch
    }
}

fun readCommits(repositoryPath: String, revString: String?, numberOfCommits: Int): List<CommitInfo> {
    val repository = configureRepository(repositoryPath)

    Git(repository).use { git ->
        val resolveQuery = revString ?: repository.fullBranchWithWorkTree
        val startCommitObjectId = repository.resolve(resolveQuery)

        return git.log().add(startCommitObjectId).setMaxCount(numberOfCommits).call().map { commit ->
            with(commit) {
                CommitInfo(
                        hash = ObjectId.toString(id),
                        parentHashes = parents.map { ObjectId.toString(it) }.toTypedArray(),
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

fun collectActions(git: Git, commit: RevCommit): Array<FileAction> {
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
        FileAction(
                entry.changeType.toAction(),
                if (entry.changeType == DiffEntry.ChangeType.DELETE) entry.oldPath else entry.newPath)
    }.toTypedArray()
}

private fun DiffEntry.ChangeType.toAction() =
        when (this) {
            DiffEntry.ChangeType.ADD -> Action.ADD
            DiffEntry.ChangeType.MODIFY -> Action.MODIFY
            DiffEntry.ChangeType.DELETE -> Action.DELETE
            DiffEntry.ChangeType.RENAME -> Action.RENAME
            DiffEntry.ChangeType.COPY -> Action.COPY
        }