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

    modifyLog(gitHubLocation, extracts);
};

const COMMIT_CLASS_NAME = "commit";
const LINKS_CELL_CLASS_NAME = "commit-links-cell";
const COMMIT_DATA_ATTRIBUTE = "data-channel";

// language=RegExp
const COMMIT_DATA_PATTERN = new RegExp("^repo:(\\w+):commit:(\\w+)$"); // repo:{number}:commit:{hash}

export async function modifyLog(githubLocation: GitHubLocation, extracts: Array<Extract>) {
    let commitsElements = document.getElementsByClassName(COMMIT_CLASS_NAME);
    let maxIndex = Math.min(commitsElements.length, 10);

    for (let i = 0; i < maxIndex; i++) {
        let commitElement = commitsElements.item(i);

        let linksCellElements = commitElement.getElementsByClassName(LINKS_CELL_CLASS_NAME);
        if (linksCellElements.length != 1) continue;

        let dataAttribute = commitElement.getAttribute(COMMIT_DATA_ATTRIBUTE);
        if (!dataAttribute) continue;

        let [repoId, hash] = parseCommitData(dataAttribute);
        let commitInfo = await fetchCommitData(githubLocation.owner, githubLocation.repo, hash);

        for (let extract_ of extracts) {
            let extractLabel = extract.core.assignLabel(commitInfo, extract_);
            if (extractLabel != null) {
                let linksCellElement = linksCellElements.item(0);
                linksCellElement.appendChild(createExtractTag(extractLabel));
            }
        }
    }
}

function parseCommitData(commitData: string): [string, string] | null {
    let matched = COMMIT_DATA_PATTERN.exec(commitData);
    if (matched == null) return null;
    return [matched[1], matched[2]];
}

function createExtractTag(extractLabel: ExtractLabel) {
    let tagSpan = document.createElement("span");

    let text = extractLabel.text ? extractLabel.text : extractLabel.name;
    let hint = extractLabel.hint ? extractLabel.hint : text;

    tagSpan.className = "extract-tag " + (extractLabel.style ? extractLabel.style : "e1");
    tagSpan.title = hint;
    tagSpan.innerText = text;

    return tagSpan;
}