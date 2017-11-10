package extract.cli

import extract.core.*
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

private const val TEMPLATE_PATH = "cli/src/main/resources/log-template"

data class LogToHtml(val htmlText: String)

data class GenerateOptions(
        val repositoryName: String,
        val gitPath: String,
        val extractsFilePath: String,
        val numberOfCommits: Int
)

fun logToHtml(generateOptions: GenerateOptions): LogToHtml {
    val templateDir = File(TEMPLATE_PATH)
    val cssFile = File(templateDir, "log.css")
    val mainFile = File(templateDir, "log.html")
    val templateFile = File(templateDir, "commit.html")
    val tagTemplateFile = File(templateDir, "tag.html")

    val mainTextTemplate = mainFile.readText()
    val commitTemplateText = templateFile.readText()
    val tagTemplateText = tagTemplateFile.readText()

    val commitsTextBuilder = StringBuilder()
    val commits = readCommits(
            generateOptions.gitPath, "refs/heads/master", generateOptions.numberOfCommits)
    val colors = HashMap<String, String>()

    val extracts = parseFile(generateOptions.extractsFilePath)
    val labelsMapping = assignLabels(commits, extracts)

    for (commit in commits) {
        val labels = labelsMapping[commit.hash]
        val tagsHtml = labels?.joinToString(separator = "\n") { label -> label.toHtml(tagTemplateText) }

        val color = colors.getOrPut(commit.author.name, { extract.cli.colors[colors.size % extract.cli.colors.size] })

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
    val htmlText = mainTextTemplate
            .replace("#css-file", cssFile.readText())
            .replace("<!--title-->", generateOptions.repositoryName)
            .replace("<!--commits-->", commitsText)

    return LogToHtml(htmlText)
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

private fun String.escapeHTML(): String {
    return map { ch ->
        if (ch.toInt() > 127 || ch == '"' || ch == '<' || ch == '>' || ch == '&') {
            "&#${ch.toInt()};"
        } else {
            ch.toString()
        }
    }.joinToString(separator = "")
}
