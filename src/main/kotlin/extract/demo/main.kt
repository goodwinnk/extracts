package extract.demo

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import java.io.File


fun main(args: Array<String>) {
    val repository = FileRepository(File("C:/Projects/kotlin/.git"))
    val git = Git(repository)
    val masterBranch = repository.resolve("refs/heads/master")

    for (commit in git.log().add(masterBranch).setMaxCount(50).call()) {
        println(commit.shortMessage)
    }
}