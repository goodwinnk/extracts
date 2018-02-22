import * as RestClient from "another-rest-client";
import {extract} from "core-js";
import FileAction = extract.core.FileAction;
import Action = extract.core.Action;
import CommitInfo = extract.core.CommitInfo;
import User = extract.core.User;

let api = new RestClient('https://api.github.com');
api.res(
    {repos: ['releases', 'commits', 'contents']}
);

export async function fetchCommitData(owner: string, repo: string, commitHash: string): Promise<CommitInfo> {
    let requestPromise = api.repos(`${owner}/${repo}`).commits(commitHash).get() as Promise<any>;
    return requestPromise.then(
        function (commitData: any) {
            let fileActions = commitData.files.map((fileData: any): FileAction => {
                let action;
                switch (fileData.status) {
                    case "modified":
                        action = Action.MODIFY;
                        break;
                    case "added":
                        action = Action.ADD;
                        break;
                    case "removed":
                        action = Action.DELETE;
                        break;
                    case "renamed":
                        action = Action.RENAME;
                        break;
                    default:
                        throw new Error(`Unknown file status: ${fileData.status}`);
                }

                return new FileAction(action, fileData.filename);
            });

            return new CommitInfo(
                commitData.sha,
                commitData.parents.map((parentData: any): string => {
                    return parentData.sha
                }),
                <User>commitData.commit.author,
                <User>commitData.commit.committer,
                commitData.commit.committer.date,
                (<string>commitData.commit.message).split('\n')[0],
                commitData.commit.message,
                fileActions
            );
        },
        function (reason: any): CommitInfo {
            return null;
        });
}

// GET /repos/:owner/:repo/contents/:path
export async function fetchFileContent(owner: string, repo: string, path: string): Promise<string> {
    let requestPromise = api.repos(`${owner}/${repo}`).contents(path).get() as Promise<any>;
    return requestPromise.then(
        function (contentData: any): string {
            let content = contentData.content;
            if (content == null) {
                return null;
            }

            return atob(content as string);
        },
        function (reason: any): string {
            return null;
        });
}
