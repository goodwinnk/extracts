package extract.core

@JsName("assignLabel")
fun assignLabelJS(commitInfo: CommitInfo, ex: Extract): ExtractLabel? {
    return extract.core.assignLabel(commitInfo, ex)
}

