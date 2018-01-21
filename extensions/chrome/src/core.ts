export enum Action {
    ADD = "ADD",
    MODIFY = "MODIFY",
    DELETE = "DELETE",
    RENAME = "RENAME",
    COPY = "COPY"
}

export class FileAction {
    constructor(readonly action: Action,
                readonly path: string) {
    }
}

export class User {
    constructor(readonly name: string,
                readonly email: string) {
    }
}

export class CommitInfo {
    constructor(readonly hash: string,
                readonly parentHashes: Array<string>,
                readonly author: User,
                readonly committer: User,
                readonly time: string,
                readonly title: string,
                readonly message: string,
                readonly fileActions: Array<FileAction>) {
    }
}

 class Extract {
     name: string;
     titlePattern: string;
     messagePattern: string;
     files: Array<string>;
     icon: string;
     text: string;
     hint: string;
     url: string;
     style: string;
     badge: string;
 }

 class ExtractLabel {
     name: string;
     text: string;
     icon: string;
     hint: string;
     url: string;
     style: string;
     badges: Array<string>
 }