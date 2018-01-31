import * as ExtractsGitHub from "./github";
import * as YamlParser from "js-yaml";
import {githubLocation, PageKind} from "./github-location";
import {fetchFileContent} from "./github";

window.onload = async function () {
    let gitHubLocation = githubLocation(location.href);
    if (gitHubLocation == null) return;

    let extractsContent = await fetchFileContent(gitHubLocation.owner, gitHubLocation.repo, ".extracts");
    if (extractsContent == null) return;

    if (gitHubLocation.kind != PageKind.commits) return;
    let parsedDocuments = YamlParser.loadAll(extractsContent) as Array<any>;

    switch (parsedDocuments.length) {
        case 1:
            modifyLog(parsedDocuments[0]);
            return;
        default:
            return;
    }
};

const COMMIT_CLASS_NAME = "commit";
const LINKS_CELL_CLASS_NAME = "commit-links-cell";
const COMMIT_DATA_ATTRIBUTE = "data-channel";

// language=RegExp
const COMMIT_DATA_PATTERN = new RegExp("^repo:(\\w+):commit:(\\w+)$"); // repo:{number}:commit:{hash}

export function modifyLog(parsedExtracts: any) {
    let commitsElements = document.getElementsByClassName(COMMIT_CLASS_NAME);
    let maxIndex = Math.min(commitsElements.length, 10);

    for (let i = 0; i < maxIndex; i++) {
        let commitElement = commitsElements.item(i);

        let linksCellElements = commitElement.getElementsByClassName(LINKS_CELL_CLASS_NAME);
        if (linksCellElements.length != 1) continue;

        let dataAttribute = commitElement.getAttribute(COMMIT_DATA_ATTRIBUTE);
        if (!dataAttribute) continue;

        let [repoId, hash] = parseCommitData(dataAttribute);
        console.log(`RepoId: ${repoId} Commit hash: ${hash}`);

        let linksCellElement = linksCellElements.item(0);
        linksCellElement.appendChild(createExtractTag("e1", "Dummy Title", "dummy"));
    }
}

function parseCommitData(commitData: string): [string, string] | null {
    let matched = COMMIT_DATA_PATTERN.exec(commitData);
    if (matched == null) return null;
    return [matched[1], matched[2]];
}

function createExtractTag(styleClass: string, title: string, extractText: string) {
    let tagSpan = document.createElement("span");
    tagSpan.className = "extract-tag " + styleClass;
    tagSpan.title = title;
    tagSpan.innerText = extractText;

    return tagSpan;
}