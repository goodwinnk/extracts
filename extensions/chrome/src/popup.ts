import {GitHubLocation, githubLocation} from "./github-location";
import {saveCustomExtractsFile, updateExtracts, loadExtractsFile, dropCustomExtractsFile} from "./background";

const EXTRACT_EDITOR_ID = "extracts-editor";
const APPLY_BUTTON_ID = "editor-form";
const SAVE_CUSTOM_BUTTON_ID = "save-custom";
const DROP_CUSTOM_BUTTON_ID = "drop-custom";

let gitHubLocation: GitHubLocation = null;
let tabId: number = null;
let extractEditorElement: HTMLTextAreaElement = null;

window.onload = async function () {
    chrome.tabs.query({active: true, currentWindow: true}, async (tabs: Array<chrome.tabs.Tab>) => {
        let tab = tabs[0];
        tabId = tab.id;

        gitHubLocation = githubLocation(tab.url);
        if (gitHubLocation == null) return;

        extractEditorElement = document.getElementById(EXTRACT_EDITOR_ID) as HTMLTextAreaElement;

        reloadExtractsText();

        document.getElementById(APPLY_BUTTON_ID).addEventListener("click", (mouseEvent: MouseEvent) => {
            applyExtractsText();
            return false;
        });

        document.getElementById(SAVE_CUSTOM_BUTTON_ID).addEventListener("click", (mouseEvent: MouseEvent) => {
            onCustomSettingsSave();
            return false;
        });

        document.getElementById(DROP_CUSTOM_BUTTON_ID).addEventListener("click", (mouseEvent: MouseEvent) => {
            onDropCustomSettings();
            return false;
        });
    });
};

async function reloadExtractsText() {
    let extractsFile = await loadExtractsFile(gitHubLocation);
    let text = extractsFile ? extractsFile : "";
    extractEditorElement.innerHTML = text;
}

function onDropCustomSettings() {
    dropCustomExtractsFile(gitHubLocation);
    reloadExtractsText();
}

function onCustomSettingsSave() {
    let innerText = extractEditorElement.value;
    saveCustomExtractsFile(innerText, gitHubLocation);
}

function applyExtractsText() {
    let innerText = extractEditorElement.value;
    updateExtracts(innerText, tabId, gitHubLocation);
}