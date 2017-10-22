package extract

import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.properties.Delegates

fun assignLabels(commitInfos: List<CommitInfo>, extracts: Extracts): Map<String, List<ExtractLabel>> {
    return commitInfos.associateBy(
            keySelector = { commitInfo -> commitInfo.hash },
            valueTransform = { commitInfo -> assignLabels(commitInfo, extracts) }
    )
}

fun assignLabels(commitInfo: CommitInfo, extracts: Extracts): List<ExtractLabel> {
    return extracts.extracts.map { assignLabel(commitInfo, it) }.filterNotNull()
}

fun assignLabel(commitInfo: CommitInfo, extract: Extract): ExtractLabel? {
    val values = MutablePredefinedVariables(commitInfo)
    val titlePattern = extract.titlePattern
    if (titlePattern != null) {
        val titleCompiledPattern = Pattern.compile(titlePattern)
        val matcher = titleCompiledPattern.matcher(commitInfo.title)
        if (matcher.find()) {
            return extract.createExtractLabel(matcher, values)
        }
    }

    if (extract.files.isNotEmpty()) {
        val isActionMatches = if (extract.hasVariable(PredefinedVariables.MATCHES)) {
            values.matches = commitInfo.fileActions.count { fileAction -> pathMatch(fileAction.path, extract.files) }
            values.matches != 0
        } else {
            commitInfo.fileActions.any { fileAction -> pathMatch(fileAction.path, extract.files) }
        }

        if (isActionMatches) {
            return extract.createExtractLabel(null, values)
        }
    }

    return null
}

fun Extract.createExtractLabel(matcher: Matcher?, values: PredefinedValues): ExtractLabel {
    return ExtractLabel(
            name,
            text = text?.rewrite(matcher, values),
            icon = icon,
            hint = hint?.rewrite(matcher, values),
            url = url?.rewrite(matcher, values),
            style = style,
            badges = listOf(badge?.rewrite(matcher, values)).filterNotNull()
    )
}

private fun Extract.hasVariable(variableName: String): Boolean {
    fun String?.has(substring: String) = this?.contains(substring) ?: false

    val template = toTemplate(variableName)
    return text.has(template) || hint.has(template) || url.has(template) || badge.has(template)
}

private fun String.rewrite(matcher: Matcher?, values: PredefinedValues): String {
    if (!this.contains("\${")) {
        return this
    }

    var result = rewrite(values)

    if (matcher != null) {
        for (i in (matcher.groupCount() downTo 0)) {
            result = result.replace(toTemplate(i.toString()), matcher.group(i))
        }
    }

    return result
}

private fun String.rewrite(values: PredefinedValues): String {
    if (!this.contains("\${")) {
        return this
    }

    return this
            .rewriteVariable(PredefinedVariables.COUNT, values.count)
            .rewriteOptionalVariable(PredefinedVariables.MATCHES, { values.matches })
}

private fun String.rewriteVariable(name: String, value: Any): String {
    return replace(toTemplate(name), value.toString())
}

private inline fun String.rewriteOptionalVariable(name: String, value: () -> Any): String {
    return if (contains(toTemplate(name))) {
        rewriteVariable(name, value())
    } else {
        this
    }
}

private fun toTemplate(name: String) = "\${$name}"

fun pathMatch(path: String, patterns: List<String>): Boolean {
    return patterns.any { pattern ->
        when {
            pattern.startsWith("**") -> path.endsWith(pattern.removePrefix("**"))
            pattern.endsWith("**") -> path.startsWith(pattern.removeSuffix("**"))
            else -> path == pattern
        }
    }
}

private class MutablePredefinedVariables(val commitInfo: CommitInfo) : PredefinedValues {
    override val count: Int
        get() = commitInfo.fileActions.size

    override var matches by Delegates.notNull<Int>()
}

