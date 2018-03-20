import {GitHubLocation, githubLocation, PageKind} from "./github-location";
import {fetchFileContent} from "./github";
import {parseExtracts} from "./parser";
import {UpdateExtractsEvent} from "./events";

chrome.tabs.onUpdated.addListener(async (tabId, changeInfo, tabInfo) => {
    let testVal = await localStorageGet("some") as string;
    console.log(`Ahha! ${testVal}`);

    await localStorageSet("some", "testValue");

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

    let extractText = await fetchFileContent(gitHubLocation.owner, gitHubLocation.repo, ".extracts");
    if (extractText == null) return;

    if (gitHubLocation.kind != PageKind.commits) return;

    updateExtracts(extractText, tabId, gitHubLocation);
});

export function updateExtracts(extractsText: string, tabId: number, gitHubLocation: GitHubLocation) {
    let extracts = parseExtracts(extractsText);
    if (extracts.length == 0) {
        // Some errors should be reported
    }

    chrome.tabs.sendMessage(tabId, new UpdateExtractsEvent(gitHubLocation, extracts));
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
                resolve(result[key] as T);
            }
        })
    });
}