import * as RestClient from "another-rest-client";
import {User, Action, FileAction, CommitInfo} from "./core";

export function fetchCommitData(owner: string, repo: string, commitHash: string) {
    let api = new RestClient('https://api.github.com');
    api.res(
        {repos: ['releases', 'commits']}
    );

    api.repos(`${owner}/${repo}`).commits(commitHash).get().then(function (commitData: any) {
        let fileActions = commitData.files.map((fileData: any): FileAction => {
            let action;
            switch (fileData.status) {
                case "modified":
                    action = Action.MODIFY;
                    break;
                case "added":
                    action = Action.ADD;
                    break;
                default:
                    throw new Error(`Unknown file status: ${fileData.status}`);
            }

            return new FileAction(action, fileData.filename);
        });

        let commitInfo = new CommitInfo(
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
        console.log(commitInfo)
    });
}