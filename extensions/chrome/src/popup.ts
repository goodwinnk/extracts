import {isExtractLoadedEvent} from "./events";
import {fetchFileContent} from "./github";
import {githubLocation} from "./github-location";

const EXTRACT_EDITOR_ID = "extracts-editor";

chrome.runtime.onMessage.addListener(
    function(request, sender, sendResponse) {
        console.log("GOT!");
        if (isExtractLoadedEvent(request)) {
            document.getElementById(EXTRACT_EDITOR_ID).innerText = request.extractsText ? request.extractsText : "";
        }
    }
);

window.onload = async function () {
    console.log(`LOADED!: ${location.href}`);

    let gitHubLocation = githubLocation(location.href);
    if (gitHubLocation == null) return;

    console.log("GitHubLocation!");

    let extractsContent = await fetchFileContent(gitHubLocation.owner, gitHubLocation.repo, ".extracts");

    let text = extractsContent ? extractsContent : "";
    console.log(text);

    document.getElementById(EXTRACT_EDITOR_ID).innerText = text;
};

function applyExtractsText(formData: any) {
    console.log(formData.extracts);
}
