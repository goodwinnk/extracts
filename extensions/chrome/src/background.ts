import {ConnectedEvent, isConnectionEvent, ExtractsLoadedEvent} from "./events";

chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        if (isConnectionEvent(request)) {
            chrome.pageAction.show(sender.tab.id);
            chrome.runtime.sendMessage(new ExtractsLoadedEvent(request.extractsText))
        }
    }
);