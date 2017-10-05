package extract.demo

import java.io.File

fun main(args: Array<String>) {
    val demoDir = File("src/test/resources/demo")
    demoDir.deleteRecursively()
    demoDir.mkdir()

    val templateDir = File("src/test/resources/log-template")
    val cssFile = File(templateDir, "log.css")
    val mainFile = File(templateDir, "log.html")
    val templateFile = File(templateDir, "commit.html")

    val cssOutFile = File(demoDir, "log.css")
    cssFile.copyTo(cssOutFile)

    val mainTextTemplate = mainFile.readText()
    val commitTemplateText = templateFile.readText()

    val commitText = commitTemplateText
            .replace("<!--title-->", "Commit title")
            .replace("<!--author-->", "author")
            .replace("<!--date-->", "2017, Jun 1")

    val commitsText = commitText.repeat(5, "\n\n")
    val mainText = mainTextTemplate.replace("<!--commits-->", commitsText)

    val logOutFile = File(demoDir, "log.html")
    logOutFile.createNewFile()
    logOutFile.writeText(mainText)

//    val commits = readCommits(".git", "refs/heads/master", 50)
}

fun CharSequence.repeat(n: Int, separator: String): String {
    require(n >= 0) { "Count 'n' must be non-negative, but was $n." }
    if (separator.isEmpty()) {
        return repeat(n)
    }

    return when (n) {
        0 -> ""
        1 -> this.toString()
        else -> {
            when (length) {
                0 -> "" // empty string if base is empty
                else -> {
                    val sb = StringBuilder(n * length + (n - 1) * separator.length)
                    sb.append(this)
                    for (i in 2..n) {
                        sb.append(separator)
                        sb.append(this)
                    }
                    sb.toString()
                }
            }
        }
    }
}