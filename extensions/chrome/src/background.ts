import {githubLocation, PageKind} from "./github-location";
import {fetchFileContent} from "./github";
import {parseExtracts} from "./parser";
import {UpdateExtractsEvent} from "./events";

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

    let extractsContent = await fetchFileContent(gitHubLocation.owner, gitHubLocation.repo, ".extracts");
    if (extractsContent == null) return;

    if (gitHubLocation.kind != PageKind.commits) return;
    let extracts = parseExtracts(extractsContent);
    if (extracts.length == 0) {
        return;
    }

    chrome.tabs.sendMessage(tabId, new UpdateExtractsEvent(gitHubLocation, extracts));
});