package extract.core

import kotlin.properties.Delegates

fun assignLabels(commitInfos: List<CommitInfo>, extracts: Extracts): Map<String, List<ExtractLabel>> {
    return commitInfos.associateBy(
            keySelector = { commitInfo -> commitInfo.hash },
            valueTransform = { commitInfo -> assignLabels(commitInfo, extracts) }
    )
}

fun assignLabels(commitInfo: CommitInfo, extracts: Extracts): List<ExtractLabel> {
    return extracts.extracts.mapNotNull { assignLabel(commitInfo, it) }
}

fun matchedPaths(commitInfo: CommitInfo, extracts: Extracts, labels: Array<ExtractLabel>): List<FileActionMatch> {
    val nameToExtract = extracts.extracts.associateBy { extract -> extract.name }

    val labelToExtract = labels.map { it to nameToExtract[it.name] }

    return commitInfo.fileActions.map { fileAction ->
        val matchedLabels = labelToExtract.filter { (_, extract) ->
            if (extract == null) return@filter false
            pathMatch(fileAction.path, extract.files)
        }.map { (label, _) -> label }

        FileActionMatch(fileAction, matchedLabels.toTypedArray())
    }
}

fun assignLabel(commitInfo: CommitInfo, extract: Extract): ExtractLabel? {
    val values = MutablePredefinedVariables(commitInfo)

    run {
        val titlePattern = extract.titlePattern
        if (titlePattern != null) {
            val titleCompiledRegex = Regex(titlePattern)
            val matchResult = titleCompiledRegex.matchEntire(commitInfo.title)
            if (matchResult != null) {
                return extract.createExtractLabel(matchResult, values)
            }
        }
    }

    run {
        val messagePattern = extract.messagePattern
        if (messagePattern != null) {
            val messageCompiledRegex = Regex(messagePattern)
            for (line in commitInfo.message.lines()) {
                val matchResult = messageCompiledRegex.matchEntire(line)
                if (matchResult != null) {
                    return extract.createExtractLabel(matchResult, values)
                }
            }
        }
    }

    run {
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
    }

    return null
}

fun Extract.createExtractLabel(matchResult: MatchResult?, values: PredefinedValues): ExtractLabel {
    return ExtractLabel(
            name,
            text = text?.rewrite(matchResult, values),
            icon = icon,
            hint = hint?.rewrite(matchResult, values),
            url = url?.rewrite(matchResult, values),
            style = style,
            badges = listOfNotNull(badge?.rewrite(matchResult, values)).toTypedArray()
    )
}

private fun Extract.hasVariable(variableName: String): Boolean {
    fun String?.has(substring: String) = this?.contains(substring) ?: false

    val template = toTemplate(variableName)
    return text.has(template) || hint.has(template) || url.has(template) || badge.has(template)
}

private fun String.rewrite(matcher: MatchResult?, values: PredefinedValues): String {
    if (!this.contains("\${")) {
        return this
    }

    var result = rewrite(values)

    if (matcher != null) {
        val groups = matcher.groups
        for (i in (groups.size - 1 downTo 0)) {
            result = result.replace(toTemplate(i.toString()), groups[i]!!.value)
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

fun pathMatch(path: String, patterns: Array<String>): Boolean {
    return patterns.any { pattern ->
        val startsWithStars = pattern.startsWith("**")
        val endsWithStars = pattern.endsWith("**")

        when {
            startsWithStars && endsWithStars -> path.contains(pattern.removePrefix("**").removeSuffix("**"))
            startsWithStars -> path.endsWith(pattern.removePrefix("**"))
            endsWithStars -> path.startsWith(pattern.removeSuffix("**"))
            else -> path == pattern
        }
    }
}

private class MutablePredefinedVariables(val commitInfo: CommitInfo) : PredefinedValues {
    override val count: Int
        get() = commitInfo.fileActions.size

    override var matches by Delegates.notNull<Int>()
}

