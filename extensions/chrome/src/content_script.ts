import * as YamlParser from "js-yaml";
import {GitHubLocation, githubLocation, PageKind} from "./github-location";
import {fetchCommitData, fetchFileContent} from "./github";
import {parseExtracts} from "./parser";
import {extract} from "core-js";
import Extract = extract.core.Extract;
import ExtractLabel = extract.core.ExtractLabel;

window.onload = async function () {
    let gitHubLocation = githubLocation(location.href);
    if (gitHubLocation == null) return;

    let extractsContent = await fetchFileContent(gitHubLocation.owner, gitHubLocation.repo, ".extracts");
    if (extractsContent == null) return;

    if (gitHubLocation.kind != PageKind.commits) return;
    let extracts = parseExtracts(extractsContent);
    if (extracts.length == 0) {
        return;
    }

    // noinspection JSIgnoredPromiseFromCall
    modifyLog(0, gitHubLocation, extracts);
};

const COMMIT_CLASS_NAME = "commit";
const COMMIT_TITLE_CELL_CLASS_NAME = "commit-title";
const COMMIT_DATA_ATTRIBUTE = "data-channel";
const MAX_LOAD_NUMBER = 10;
const EXTRACTS_LOAD_MORE_LINK_CLASS_NAME = "extracts_load_more_link";

// language=RegExp
const COMMIT_DATA_PATTERN = new RegExp("^repo:(\\w+):commit:(\\w+)$"); // repo:{number}:commit:{hash}

export async function modifyLog(index: number, githubLocation: GitHubLocation, extracts: Array<Extract>) {
    let commitsElements = document.getElementsByClassName(COMMIT_CLASS_NAME);
    let maxIndex = Math.min(commitsElements.length, index + MAX_LOAD_NUMBER);

    for (let i = index; i < maxIndex; i++) {
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

    let loadMoreIndex = maxIndex;
    if (commitsElements.length > loadMoreIndex ) {
        let commitElement = commitsElements.item(loadMoreIndex);

        let titleElements = commitElement.getElementsByClassName(COMMIT_TITLE_CELL_CLASS_NAME);
        if (titleElements.length > 0) {
            let titleElement = titleElements.item(0);
            titleElement.appendChild(createLoadMoreRef(loadMoreIndex, githubLocation, extracts))
        }
    }
}

function createLoadMoreRef(index: number, githubLocation: GitHubLocation, extracts: Array<Extract>): HTMLElement {
    let refElement: HTMLElement = document.createElement("span");

    refElement.innerText = "Load extracts...";
    refElement.className = EXTRACTS_LOAD_MORE_LINK_CLASS_NAME;
    refElement.onclick = function () {
        onLoadMoreClick(index, githubLocation, extracts);
    };

    return refElement;
}

function onLoadMoreClick(index: number, githubLocation: GitHubLocation, extracts: Array<Extract>) {
    let commitsElements = document.getElementsByClassName(COMMIT_CLASS_NAME);
    if (commitsElements.length <= index) {
        return;
    }

    let commitElement = commitsElements.item(index);
    let titleElements = commitElement.getElementsByClassName(COMMIT_TITLE_CELL_CLASS_NAME);
    if (titleElements.length != 1) return;

    let titleElement = titleElements.item(0);
    let loadMoreElements = titleElement.getElementsByClassName(EXTRACTS_LOAD_MORE_LINK_CLASS_NAME);
    if (titleElements.length == 1) {
        let loadMoreElement = loadMoreElements.item(0);
        loadMoreElement.remove()
    }

    modifyLog(index, githubLocation, extracts);
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