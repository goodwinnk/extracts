import { githubLocation, GitHubLocation, PageKind } from '../src/github-location';
import { expect } from 'chai';
import 'mocha';

describe('GitHub Location Parsing', () => {
    it('simple parsing for commits page', () => {
        const result = githubLocation("https://github.com/JetBrains/kotlin/commits/master");
        let expected = new GitHubLocation(PageKind.commits, "JetBrains", "kotlin", "master");
        expect(result).to.deep.equals(expected);
    });

    it('branch with slashes', () => {
        const result = githubLocation("https://github.com/JetBrains/kotlin/commits/rr/nk");
        expect(result).to.deep.equal(new GitHubLocation(PageKind.commits, "JetBrains", "kotlin", "rr/nk"));
    });

    it('should have default branch', () => {
        const result = githubLocation("https://github.com/JetBrains/kotlin/commits");
        expect(result).to.deep.equal(new GitHubLocation(PageKind.commits, "JetBrains", "kotlin", "master"));
    });

    it('should not give valid commits position', () => {
        const result = githubLocation("https://github.com/JetBrains/kotlin/diff");
        expect(result).to.deep.equal(null);
    });
});