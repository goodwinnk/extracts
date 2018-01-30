// https://github.com/{owner}/{repo}/commits/{branch}
// language=RegExp
const COMMITS_PAGE_URL_PATTERN = new RegExp("^[^?]*github.com/([^/]*)/([^/]*)/commits/?([^?]*)?(\\?.*)?$");

export enum PageKind {
    commits
}

export class GitHubLocation {
    constructor(
        readonly kind: PageKind,
        readonly owner: string,
        readonly repo: string,
        readonly branch: string = "master"
    ) {}
}

export function githubLocation(location: string): GitHubLocation {
    let matched = COMMITS_PAGE_URL_PATTERN.exec(location);
    if (matched == null) return null;
    let owner = matched[1];
    let repo = matched[2];
    let branch = matched[3];
    return new GitHubLocation(PageKind.commits, owner, repo, branch);
}