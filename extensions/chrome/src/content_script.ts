import {GitHubLocation} from "./github-location";
import {fetchCommitData} from "./github";
import {extract} from "core-js";
import {isUpdateExtractEvent} from "./events";
import Extract = extract.core.Extract;
import ExtractLabel = extract.core.ExtractLabel;

chrome.runtime.onMessage.addListener(request => {
    if (isUpdateExtractEvent(request)) {
        // noinspection JSIgnoredPromiseFromCall
        updateExtracts(request.githubLocation, request.extracts);
    }
});

const COMMIT_CLASS_NAME = "commit";
const COMMIT_TITLE_CELL_CLASS_NAME = "commit-title";
const COMMIT_DATA_ATTRIBUTE = "data-channel";

// language=RegExp
const COMMIT_DATA_PATTERN = new RegExp("^repo:(\\w+):commit:(\\w+)$"); // repo:{number}:commit:{hash}

export async function updateExtracts(githubLocation: GitHubLocation, extracts: Array<Extract>) {
    let commitsElements = document.getElementsByClassName(COMMIT_CLASS_NAME);

    for (let i = 0; i < commitsElements.length; i++) {
        let commitElement = commitsElements.item(i);

        let titleElements = commitElement.getElementsByClassName(COMMIT_TITLE_CELL_CLASS_NAME);
        if (titleElements.length != 1) continue;
        let titleElement = titleElements.item(0);

        let dataAttribute = commitElement.getAttribute(COMMIT_DATA_ATTRIBUTE);
        if (!dataAttribute) continue;

        let [_, hash] = parseCommitData(dataAttribute);
        let commitInfo = await fetchCommitData(githubLocation.owner, githubLocation.repo, hash);

        for (let extract_ of extracts) {
            let extractLabel = extract.core.assignLabel(commitInfo, extract_);
            if (extractLabel != null) {
                titleElement.appendChild(createExtractLabelElement(extractLabel));
            }
        }
    }
}

function parseCommitData(commitData: string): [string, string] | null {
    let matched = COMMIT_DATA_PATTERN.exec(commitData);
    if (matched == null) return null;
    return [matched[1], matched[2]];
}

function createExtractLabelElement(extractLabel: ExtractLabel): HTMLElement {
    let tagSpan: HTMLElement = document.createElement("span");

    let text = extractLabel.text ? extractLabel.text : extractLabel.name;
    let hint = extractLabel.hint ? extractLabel.hint : text;

    tagSpan.className = "extract-tag " + (extractLabel.style ? extractLabel.style : "e1");
    tagSpan.title = hint;
    tagSpan.innerText = text;

    return tagSpan;
}