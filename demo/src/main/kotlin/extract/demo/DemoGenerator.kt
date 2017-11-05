package extract.demo

import extract.cli.GenerateOptions
import extract.cli.logToHtml
import java.io.File

private const val OUT_PATH = "demo/out/extracts"
private const val RESOURCES_PATH = "demo/src/main/resources"

data class DemoRepository(val name: String, val gitPath: String, val yamlPath: String, val generate: Boolean)

fun main(args: Array<String>) {
    val gitFile = File("$RESOURCES_PATH/demo-git.txt")
    var repositories = if (gitFile.exists()) {
        gitFile.readLines().asSequence()
                .map { it.trim() }
                .filter { !it.isEmpty() }
                .map { line ->
                    val params = line.split(" ").map { it.trim() }
                    if (params.size !in 3..4) {
                        throw IllegalStateException("Bad line in '$line' in $gitFile")
                    }

                    val shouldGenerate = params.getOrNull(3) == "*"
                    DemoRepository(params[0], params[1], params[2], shouldGenerate)
                }
                .toList()
    } else {
        listOf()
    }

    if (repositories.isEmpty()) {
        repositories = listOf(DemoRepository("extract", ".git", "$RESOURCES_PATH/default.yaml", true))
    }

    val repositoriesToGenerate = repositories.filter { it.generate }
    if (!repositoriesToGenerate.isEmpty()) {
        val demoDir = File(OUT_PATH)
        demoDir.deleteRecursively()
        demoDir.mkdir()

        repositoriesToGenerate.forEach {
            generateForRepository(it)
        }
    }
}

private fun generateForRepository(repository: DemoRepository) {
    val repoOutDir = File(OUT_PATH, repository.name)
    repoOutDir.mkdir()

    val htmlOutput = logToHtml(
            GenerateOptions(
                    repositoryName = repository.name,
                    gitPath = repository.gitPath,
                    extractsFilePath = "$RESOURCES_PATH/${repository.yamlPath}"
            )
    )

    val cssOutFile = File(repoOutDir, "log.css")
    cssOutFile.createNewFile()
    cssOutFile.writeText(htmlOutput.cssText)

    val logOutFile = File(repoOutDir, "log.html")
    logOutFile.createNewFile()
    logOutFile.writeText(htmlOutput.htmlText)
}