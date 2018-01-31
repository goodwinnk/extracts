import {expect} from 'chai';
import 'mocha';
import {fetchFileContent} from "../src/github";
import {nodify} from "./nodify";

nodify();

describe('GitHub API: Fetch File Content', () => {
    it('request existing file content', async () => {
        let readmeContent = await fetchFileContent("goodwinnk", "extract", "README.md");
        expect(readmeContent).to.contain("Motivation");
    });

    it('request absent file content', async () => {
        let absentContent = await fetchFileContent("goodwinnk", "extract", "NoSuchFile");
        expect(absentContent).to.equals(null);
    });
});