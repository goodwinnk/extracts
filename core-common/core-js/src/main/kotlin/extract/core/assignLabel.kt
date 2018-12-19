package extract.core

@JsName("assignLabel")
fun assignLabelJS(commitInfo: CommitInfo, ex: Extract): ExtractLabel? {
    return extract.core.assignLabel(commitInfo, ex)
}

@JsName("assignLabelsWithConfig")
fun assignLabelsJS(commitInfo: CommitInfo, extractsConfig: ExtractsConfig): Array<ExtractLabel> {
    return extract.core.assignLabels(commitInfo, extractsConfig).toTypedArray()
}

