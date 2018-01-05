namespace ExtractsCore {
    enum Action {
        ADD,
        MODIFY,
        DELETE,
        RENAME,
        COPY
    }

    class FileAction {
        action: Action;
        path: string
    }

    class User {
        name: string;
        email: string
    }

    class CommitInfo {
        hash: string;
        parentHashes: Array<string>;

        author: User;
        committer: User;

        time: number;
        title: string;
        message: string;

        fileActions: Array<FileAction>;
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
}