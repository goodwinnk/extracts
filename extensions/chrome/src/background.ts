import {GitHubLocation, githubLocation, PageKind} from "./github-location";
import {fetchFileContent} from "./github";
import {parseExtracts} from "./parser";
import {UpdateExtractsEvent} from "./events";

const CUSTOM = "custom";

class RepoSettings {
    constructor(
        readonly template: string,
        readonly extractsFile: string
    ) {}
}

chrome.tabs.onUpdated.addListener(async (tabId, changeInfo, tabInfo) => {
    if (changeInfo.status != "complete") {
        chrome.pageAction.hide(tabId);
        return;
    }

    let url = tabInfo.url;

    let gitHubLocation = githubLocation(url);
    if (gitHubLocation == null) {
        chrome.pageAction.hide(tabId);
        return;
    }

    chrome.pageAction.show(tabId);

    if (gitHubLocation.kind != PageKind.commits) return;

    let extractsFile = await loadExtractsFile(gitHubLocation);
    if (!extractsFile) return;

    updateExtracts(extractsFile, tabId, gitHubLocation);
});

export async function loadExtractsFile(gitHubLocation: GitHubLocation): Promise<string> {
    let repoKey = storageKey(gitHubLocation);
    let repoSettings = await localStorageGet<RepoSettings>(repoKey);

    let extractsFile: string;
    if (repoSettings && repoSettings.template == CUSTOM) {
        extractsFile = repoSettings.extractsFile;
    } else {
        extractsFile = await fetchFileContent(gitHubLocation.owner, gitHubLocation.repo, ".extracts")
    }

    return extractsFile
}

function storageKey(gitHubLocation: GitHubLocation) {
    return `${gitHubLocation.owner}_${gitHubLocation.repo}`;
}

export function updateExtracts(extractsText: string, tabId: number, gitHubLocation: GitHubLocation) {
    let extracts = parseExtracts(extractsText);
    if (extracts.length == 0) {
        // Some errors should be reported
    }

    chrome.tabs.sendMessage(tabId, new UpdateExtractsEvent(gitHubLocation, extracts));
}

export function saveCustomExtractsFile(extractsText: string, gitHubLocation: GitHubLocation) {
    let repoKey = storageKey(gitHubLocation);
    let repoSettings = new RepoSettings(CUSTOM, extractsText);

    localStorageSet(repoKey, repoSettings);
}

export function dropCustomExtractsFile(gitHubLocation: GitHubLocation) {
    let repoKey = storageKey(gitHubLocation);
    localStorageRemove(repoKey);
}

async function localStorageSet(key: string, items: any): Promise<void> {
    return new Promise<void>((resolve, reject) => {
        let item = {};
        item[key] = JSON.stringify(items);
        chrome.storage.local.set(item, () => {
            if (chrome.runtime.lastError) {
                reject();
            } else {
                resolve();
            }
        })
    });
}

async function localStorageGet<T>(key: string): Promise<T> {
    return new Promise<T>((resolve, reject) => {
        chrome.storage.local.get([key], (result: any) => {
            if (chrome.runtime.lastError) {
                reject();
            } else {
                let objStr = result[key];
                if (objStr) {
                    resolve(JSON.parse(objStr) as T);
                } else {
                    resolve(null);
                }
            }
        })
    });
}

function localStorageRemove<T>(key: string) {
    chrome.storage.local.remove([key]);
}