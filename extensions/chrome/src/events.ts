import {GitHubLocation} from "./github-location";
import {extract} from "core-js";
import Extract = extract.core.Extract;

enum EventKind {
    EXTRACTS_LOADED,
    UPDATE_EXTRACTS
}

export class ExtractsLoadedEvent {
    readonly kind = EventKind.EXTRACTS_LOADED;

    constructor(readonly extractsText: string) {
    }
}

export function isExtractLoadedEvent(event: any): event is ExtractsLoadedEvent {
    return (<ExtractsLoadedEvent>event).kind === EventKind.EXTRACTS_LOADED;
}

export class UpdateExtractsEvent {
    readonly kind = EventKind.UPDATE_EXTRACTS;

    constructor(readonly githubLocation: GitHubLocation, readonly extracts: Array<Extract>) {
    }
}

export function isUpdateExtractEvent(event: any): event is UpdateExtractsEvent {
    return (<UpdateExtractsEvent>event).kind === EventKind.UPDATE_EXTRACTS;
}

