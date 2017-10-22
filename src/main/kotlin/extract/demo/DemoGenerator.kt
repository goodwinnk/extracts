package extract.demo

import extract.*
import java.io.File

private val colors = listOf(
        "PowderBlue",
        "FireBrick",
        "DarkGoldenRod",
        "DarkKhaki",
        "Silver",
        "MediumVioletRed",
        "IndianRed",
        "Peru",
        "Chocolate",
        "Tan"
)

data class DemoRepository(val name: String, val gitPath: String, val yamlPath: String, val generate: Boolean)

fun main(args: Array<String>) {
    val gitFile = File("src/main/resources/demo-git.txt")
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
        repositories = listOf(DemoRepository("extract", ".git", "src/main/resources/default.yaml", true))
    }

    repositories.filter { it.generate }.forEach {
        generateForRepository(it)
    }
}

private fun generateForRepository(repository: DemoRepository) {
    val demoDir = File("out/demo")
    demoDir.deleteRecursively()
    demoDir.mkdir()

    val repoOutDir = File(demoDir, repository.name)
    repoOutDir.mkdir()

    val templateDir = File("src/main/resources/log-template")
    val cssFile = File(templateDir, "log.css")
    val mainFile = File(templateDir, "log.html")
    val templateFile = File(templateDir, "commit.html")
    val tagTemplateFile = File(templateDir, "tag.html")

    val cssOutFile = File(repoOutDir, "log.css")
    cssFile.copyTo(cssOutFile)

    val mainTextTemplate = mainFile.readText()
    val commitTemplateText = templateFile.readText()
    val tagTemplateText = tagTemplateFile.readText()

    val commitsTextBuilder = StringBuilder()
    val commits = readCommits(repository.gitPath, "refs/heads/master", 50)
    val colors = HashMap<String, String>()

    val extracts = parseFile(repository.yamlPath)
    val labelsMapping = assignLabels(commits, extracts)

    for (commit in commits) {
        val labels = labelsMapping[commit.hash]
        val tagsHtml = labels?.joinToString(separator = "\n") { label -> label.toHtml(tagTemplateText) }

        val color = colors.getOrPut(commit.author.name, { extract.demo.colors[colors.size % extract.demo.colors.size] })

        val commitText = commitTemplateText
                .replace("<!--popup-id-->", "popup-${commit.hash}")
                .replace("<!--popup-content-->", commit.toHtml())
                .replace("<!--title-->", commit.title)
                .replace("<!--author-->", commit.author.name)
                .replace("<!--date-->", epochSecondsToString(commit.time))
                .replace("<!--author-style-->", "background: $color;")
                .replace("<!--tags-->", tagsHtml ?: "<!-- no tags -->")

        commitsTextBuilder.append(commitText)
    }

    val commitsText = commitsTextBuilder.toString()
    val mainText = mainTextTemplate.replace("<!--commits-->", commitsText)

    val logOutFile = File(repoOutDir, "log.html")
    logOutFile.createNewFile()
    logOutFile.writeText(mainText)
}

private fun ExtractLabel.toHtml(template: String): String {
    val tagClass = style ?: "e0"
    val text = text ?: name
    val labelContent = if (url != null) {
        """<a class="$tagClass" href="$url">$text</a>"""
    } else {
        text
    }
    val badgesHtml = badges.joinToString(separator = "") {
        """<span class="badge">$it</span>"""
    }

    val withBadges = "$labelContent$badgesHtml"

    return template
            .replace("<!--tag-class-->", tagClass)
            .replace("<!--hint-->", hint ?: text)
            .replace("<!--text-->", withBadges)
}

private fun CommitInfo.toHtml(): String {
    val messageHtml = message.escapeHTML().replace("\n", "<br/>")
    val actionsHtml = fileActions.joinToString(separator = "<br/>") { it.toHtml() }
    return "$messageHtml<br/>$actionsHtml"
}

private fun FileAction.toHtml(): String {
    val actionHtml = when (action) {
        Action.ADD -> "A"
        Action.MODIFY -> "M"
        Action.DELETE -> "D"
        Action.RENAME -> "R"
        Action.COPY -> "C"
    }

    return "$actionHtml: $path"
}

fun String.escapeHTML(): String {
    return map { ch ->
        if (ch.toInt() > 127 || ch == '"' || ch == '<' || ch == '>' || ch == '&') {
            "&#${ch.toInt()};"
        } else {
            ch.toString()
        }
    }.joinToString(separator = "")
}
