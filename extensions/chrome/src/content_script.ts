import {GitHubLocation} from "./github-location";
import {fetchCommitData} from "./github";
import {extract} from "core-js";
import {isUpdateExtractEvent} from "./events";
import Extract = extract.core.Extract;
import ExtractLabel = extract.core.ExtractLabel;
import CommitInfo = extract.core.CommitInfo;

chrome.runtime.onMessage.addListener(request => {
    if (isUpdateExtractEvent(request)) {
        // noinspection JSIgnoredPromiseFromCall
        updateExtracts(request.githubLocation, request.extracts);
    }
});

const COMMIT_CLASS_NAME = "commit";
const COMMIT_TITLE_CELL_CLASS_NAME = "commit-title";
const COMMIT_DATA_ATTRIBUTE = "data-channel";
const EXTRACT_CLASS_NAME = "extract-tag";

// language=RegExp
const COMMIT_DATA_PATTERN = new RegExp("^repo:(\\w+):commit:(\\w+)$"); // repo:{number}:commit:{hash}

export async function updateExtracts(githubLocation: GitHubLocation, extracts: Array<Extract>) {
    clearExtractTags();

    let commitsElements = document.getElementsByClassName(COMMIT_CLASS_NAME);

    for (let i = 0; i < commitsElements.length; i++) {
        let commitElement = commitsElements.item(i);

        let titleElement = findTitleElement(commitElement as HTMLElement);
        if (!titleElement) continue;

        let cachedCommitInfo = titleElement["commitInfo"];
        let commitInfo: CommitInfo;
        if (cachedCommitInfo) {
            commitInfo = cachedCommitInfo;
        } else {
            let dataAttribute = commitElement.getAttribute(COMMIT_DATA_ATTRIBUTE);
            if (!dataAttribute) continue;

            let [_, hash] = parseCommitData(dataAttribute);

            commitInfo = await fetchCommitData(githubLocation.owner, githubLocation.repo, hash);

            if (!commitInfo) {
                console.log(`Couldn't fetch commit information for: ${githubLocation.owner}, ${githubLocation.repo} ${hash}`);
                return;
            }
        }
        titleElement["commitInfo"] = commitInfo;

        for (let j = 0; j < extracts.length; j++) {
            let extract_ = extracts[j];
            let extractLabel = extract.core.assignLabel(commitInfo, extract_);
            if (extractLabel != null) {
                titleElement.appendChild(createExtractLabelElement(extractLabel));
            }
        }
    }
}

function findTitleElement(commitElement: HTMLElement): HTMLElement {
    let titleElements = commitElement.getElementsByClassName(COMMIT_TITLE_CELL_CLASS_NAME);
    if (titleElements.length != 1) return null;
    return titleElements.item(0) as HTMLElement;
}

function clearExtractTags() {
    let commitsElements = document.getElementsByClassName(COMMIT_CLASS_NAME);

    for (let i = 0; i < commitsElements.length; i++) {
        let titleElement = findTitleElement(commitsElements.item(i) as HTMLElement);
        if (!titleElement) continue;

        let commitExtracts = Array.from(titleElement.getElementsByClassName(EXTRACT_CLASS_NAME));
        for (let j = 0; j < commitExtracts.length; j++) {
            titleElement.removeChild(commitExtracts[j]);
        }
    }
}

function parseCommitData(commitData: string): [string, string] | null {
    let matched = COMMIT_DATA_PATTERN.exec(commitData);
    if (matched == null) return null;
    return [matched[1], matched[2]];
}

function createExtractLabelElement(extractLabel: ExtractLabel): HTMLElement {
    let extractElement: HTMLElement;
    if (extractLabel.url) {
        let anchorElement = document.createElement("a") as HTMLAnchorElement;
        anchorElement.href = extractLabel.url;
        extractElement = anchorElement;
    } else {
        extractElement = document.createElement("span");
    }

    let text = extractLabel.text ? extractLabel.text : extractLabel.name;
    let hint = extractLabel.hint ? extractLabel.hint : text;
    let styleClass = extractLabel.style ? extractLabel.style : "e0";

    extractElement.className = `${EXTRACT_CLASS_NAME} ${styleClass}`;
    extractElement.title = hint;
    extractElement.innerText = text;

    return extractElement;
}