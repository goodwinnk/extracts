package extract.cli

import extract.core.*


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

data class LogToHtml(val htmlText: String)

data class GenerateOptions(
        val repositoryName: String,
        val gitPath: String,
        val extractsFilePath: String,
        val revision: String?,
        val numberOfCommits: Int)

fun logToHtml(generateOptions: GenerateOptions): LogToHtml {
    val mainTemplateText = readResourceFile("log.html")
    val commitTemplateText = readResourceFile("commit.html")
    val tagTemplateText = readResourceFile("tag.html")
    val cssTemplateText = readResourceFile("log.css")

    val commitsTextBuilder = StringBuilder()
    val commits = readCommits(generateOptions.gitPath, generateOptions.revision, generateOptions.numberOfCommits)
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
    val htmlText = mainTemplateText
            .replace("#css-file", cssTemplateText)
            .replace("<!--title-->", generateOptions.repositoryName)
            .replace("<!--commits-->", commitsText)

    return LogToHtml(htmlText)
}

private fun readResourceFile(relativePath: String): String {
    val klass = GenerateOptions::class.java
    val resourceStream = klass.getResourceAsStream("/log-template/$relativePath")
    return resourceStream.reader().buffered().readText()
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
