window.onload = function () {
    modifyLog();
};

function modifyLog() {
    let elements = document.getElementsByClassName("commit-links-cell table-list-cell");
    console.log("Number of found commits: " + elements.length);

    let maxIndex = Math.min(elements.length, 10);

    for (let i = 0; i < maxIndex; i++) {
        let commitElement = elements.item(i);
        commitElement.appendChild(createExtractTag("e1", "Dummy Title", "dummy"));
    }
}

function createExtractTag(styleClass: string, title: string, extractText: string) {
    let tagSpan = document.createElement("span");
    tagSpan.className = "extract-tag " + styleClass;
    tagSpan.title = title;
    tagSpan.innerText = extractText;

    return tagSpan;
}