package extract.demo

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.PersonIdent
import java.io.File


fun main(args: Array<String>) {
    val repository = FileRepository(File("C:/Projects/kotlin/.git"))
    val git = Git(repository)
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
                    fileActions = listOf()
            )
        }

        println(commitInfo)
    }
}

fun PersonIdent.toUser() = User(name, emailAddress)