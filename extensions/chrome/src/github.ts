import * as RestClient from "another-rest-client";

export function fetchCommitData(owner: string, repo: string, commitHash: string) {
    let api = new RestClient('https://api.github.com');
    api.res(
        {repos: ['releases', 'commits']}
    );

    api.repos(`${owner}/${repo}`).commits(commitHash).get().then(function (commitData: any) {
        console.log(commitData.commit.message);
    });
}