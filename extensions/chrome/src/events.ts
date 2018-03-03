import {GitHubLocation} from "./github-location";

enum EventKind {
    CONNECTION,
    EXTRACTS_LOADED,
    UPDATE_EXTRACTS
}

export class ConnectedEvent {
    readonly kind = EventKind.CONNECTION;

    constructor(readonly location: GitHubLocation, readonly extractsText: string) {
    }
}

export function isConnectionEvent(event: any): event is ConnectedEvent {
    return (<ConnectedEvent>event).kind === EventKind.CONNECTION;
}

export class ExtractsLoadedEvent {
    readonly kind = EventKind.EXTRACTS_LOADED;

    constructor(readonly extractsText: string) {
    }
}

export function isExtractLoadedEvent(event: any): event is ExtractsLoadedEvent {
    return (<ExtractsLoadedEvent>event).kind === EventKind.EXTRACTS_LOADED;
}

export class UpdateExtracts {
    readonly kind = EventKind.EXTRACTS_LOADED;

    constructor(readonly extractsText: string) {
    }
}

export function isUpdateExtractEvent(event: any): event is UpdateExtracts {
    return (<UpdateExtracts>event).kind === EventKind.EXTRACTS_LOADED;
}

