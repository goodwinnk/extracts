package extract.demo

import java.util.regex.Matcher
import java.util.regex.Pattern

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
    val titlePattern = extract.titlePattern
    if (titlePattern != null) {
        val titleCompiledPattern = Pattern.compile(titlePattern)
        val matcher = titleCompiledPattern.matcher(commitInfo.title)
        if (matcher.find()) {
            return ExtractLabel(
                    extract.name,
                    text = extract.text?.rewrite(matcher),
                    icon = extract.icon,
                    hint = extract.hint?.rewrite(matcher),
                    url = extract.url?.rewrite(matcher)
            )
        }
    }

    return null
}

fun String.rewrite(matcher: Matcher): String {
    if (!this.contains("\${")) {
        return this
    }

    var result = this
    for (i in (matcher.groupCount() downTo 0)) {
        result = result.replace("\\$\\{$i}".toRegex(), matcher.group(i))
    }

    return result
}

