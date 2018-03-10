import {fetchFileContent} from "./github";
import {GitHubLocation, githubLocation} from "./github-location";
import {updateExtracts} from "./background";

const EXTRACT_EDITOR_ID = "extracts-editor";
const FORM_EDITOR_ID = "editor-form";

let gitHubLocation: GitHubLocation = null;
let tabId: number = null;
let extractEditorElement: HTMLTextAreaElement = null;

window.onload = async function () {
    chrome.tabs.query({active: true, currentWindow: true}, async (tabs: Array<chrome.tabs.Tab>) => {
        let tab = tabs[0];
        tabId = tab.id;

        gitHubLocation = githubLocation(tab.url);
        if (gitHubLocation == null) return;

        let extractsContent = await fetchFileContent(gitHubLocation.owner, gitHubLocation.repo, ".extracts");

        let text = extractsContent ? extractsContent : "";
        extractEditorElement = document.getElementById(EXTRACT_EDITOR_ID) as HTMLTextAreaElement;
        extractEditorElement.innerText = text;

        document.getElementById(FORM_EDITOR_ID).addEventListener("submit", (submitEvent: Event) => {
            submitEvent.preventDefault();
            applyExtractsText();
            return false;
        });
    });
};

function applyExtractsText() {
    let innerText = extractEditorElement.value;
    updateExtracts(innerText, tabId, gitHubLocation);
}