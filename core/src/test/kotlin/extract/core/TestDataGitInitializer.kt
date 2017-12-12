package extract.core

import org.junit.Assert
import java.io.File
import java.nio.file.Files

abstract class TestDataGitInitializer {
    var tempDir: File? = null

    abstract val basePath: String
    abstract val testClassName: String
    open val baseDirName: String = "main"
    open val workTreeDirNames: List<String> = listOf()

    fun mainGitPath(): String = File(File(tempDir!!, baseDirName), DOT_GIT).absolutePath

    open fun beforeClass() {
        val testDir = Files.createTempDirectory(testClassName).toFile()
        Assert.assertTrue(testDir.isDirectory)

        tempDir = testDir

        val baseDir = File(basePath)
        baseDir.copyRecursively(testDir, true)

        prepareTestDataRepository(testDir, baseDirName, workTreeDirNames)
    }

    open fun afterClass() {
        tempDir?.deleteRecursively()
        tempDir = null
    }
}

private const val TEMP_GIT = "temp-git"
private const val DOT_GIT = ".git"

private val File.gitFileOrDir inline get() = File(this, DOT_GIT)

private fun prepareTestDataRepository(baseTempDir: File, mainDirName: String, workTreesDirNames: List<String>) {
    val mainDir = File(baseTempDir, mainDirName)
    val mainGitDir = mainDir.gitFileOrDir

    val workTreeDirs = workTreesDirNames.map { File(baseTempDir, it) }

    File(mainDir, TEMP_GIT).renameTo(mainGitDir)

    for (workTreeDir in workTreeDirs) {
        val workTreeFile = workTreeDir.gitFileOrDir
        val workTreesDirName = workTreeDir.name

        File(workTreeDir, TEMP_GIT).renameTo(workTreeFile)

        val workTreeInMainGitDirFile = File(mainGitDir, "worktrees/$workTreesDirName/gitdir")
        val filesWithBaseDir = listOf(workTreeInMainGitDirFile, workTreeFile)
        for (file in filesWithBaseDir) {
            file.writeText(file.readText().replace("\$BASE_DIR", baseTempDir.canonicalPath))
        }
    }
}