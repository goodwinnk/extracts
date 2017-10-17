package extract.demo

import extract.*
import java.io.File

val COLORS = listOf(
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

fun main(args: Array<String>) {
    val gitFile = File("src/main/resources/demo-git.txt")
    val git = if (gitFile.exists()) {
        gitFile.readText().trim()
    } else {
        ".git"
    }

    val demoDir = File("out/demo")
    demoDir.deleteRecursively()
    demoDir.mkdir()

    val templateDir = File("src/main/resources/log-template")
    val cssFile = File(templateDir, "log.css")
    val mainFile = File(templateDir, "log.html")
    val templateFile = File(templateDir, "commit.html")
    val tagTemplateFile = File(templateDir, "tag.html")

    val cssOutFile = File(demoDir, "log.css")
    cssFile.copyTo(cssOutFile)

    val mainTextTemplate = mainFile.readText()
    val commitTemplateText = templateFile.readText()
    val tagTemplateText = tagTemplateFile.readText()

    val commitsTextBuilder = StringBuilder()
    val commits = readCommits(git, "refs/heads/master", 50)
    val colors = HashMap<String, String>()

    val extracts = parseFile("src/main/resources/kotlin.yaml")
    val labelsMapping = assignLabels(commits, extracts)

    for (commit in commits) {
        val labels = labelsMapping[commit.hash]
        val tagsHtml = labels?.map { label ->
            val tagClass = label.style ?: "e0"
            val text = label.text ?: label.name
            val labelContent = if (label.url != null) {
                """<a class="$tagClass" href="${label.url}">$text</a>"""
            } else {
                text
            }

            tagTemplateText
                    .replace("<!--tag-class-->", tagClass)
                    .replace("<!--hint-->", label.hint ?: label.text ?: label.name)
                    .replace("<!--text-->", labelContent)
        }?.joinToString(separator = "\n")

        val color = colors.getOrPut(commit.author.name, { COLORS[colors.size % COLORS.size] })

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

    val logOutFile = File(demoDir, "log.html")
    logOutFile.createNewFile()
    logOutFile.writeText(mainText)
}

private fun CommitInfo.toHtml(): String {
    val messageHtml = message.escapeHTML().replace("\n", "<br/>")
    val actionsHtml = fileActions.map { it.toHtml() }.joinToString(separator = "<br/>")
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
