declare module "core-js" {
    export namespace extract.core {
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

        export class Extract {
            readonly name: string;

            readonly titlePattern: string;
            readonly messagePattern: string;

            readonly files: Array<string>;
            readonly icon: string;
            readonly text: string;
            readonly hint: string;
            readonly url: string;
            readonly style: string;
            readonly badge: string;

            constructor(name: string,
                        titlePattern: string,
                        messagePattern: string,
                        files: Array<string>,
                        icon: string,
                        text: string,
                        hint: string,
                        url: string,
                        style: string,
                        badge: string)
        }
    }
}