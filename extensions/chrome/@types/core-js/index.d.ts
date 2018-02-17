declare module "core-js" {
    export = extracts.core
}

declare namespace extracts.core {
    export enum Action {
        ADD,
        MODIFY,
        DELETE,
        RENAME,
        COPY
    }

    export class User {
        readonly name: string;
        readonly email: string;

        constructor(name: string, email: string)
    }

    export class FileAction {
        readonly action: Action;
        readonly path: string;

        constructor(action: Action, path: string)
    }

    export class CommitInfo {
        readonly hash: string;
        readonly parentHashes: Array<string>;
        readonly author: User;
        readonly committer: User;
        readonly time: string;
        readonly title: string;
        readonly message: string;
        readonly fileActions: Array<FileAction>;

        constructor(hash: string,
                    parentHashes: Array<string>,
                    author: User,
                    committer: User,
                    time: string,
                    title: string,
                    message: string,
                    fileActions: Array<FileAction>)
    }
}