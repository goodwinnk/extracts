/*
    @Test
    fun assignLabelByBothPatterns() {
        val extract =
                Extract("Some",
                        titlePattern = "^.*(first).*$",
                        messagePattern = "^.*(second).*$",
                        text = "\${1}")

        assertEquals(
                ExtractLabel(name = "Some", text = "first", icon = null, hint = null, url = null, style = null, badges = arrayOf()),
                assignLabel(
                        testCommit(hash = "123", title = "bla bla bla first", message = "bla bla bla first\n\nfoo foo foo first second"),
                        extract)
        )

        assertEquals(
                ExtractLabel(name = "Some", text = "second", icon = null, hint = null, url = null, style = null, badges = arrayOf()),
                assignLabel(
                        testCommit(hash = "345", title = "bla bla bla", message = "bla bla bla\n\nfoo foo foo first second"),
                        extract)
        )
    }

 */

import 'mocha';
import {expect} from 'chai';
import {extract} from "core-js";
import Extract = extract.core.Extract;
import ExtractLabel = extract.core.ExtractLabel;
import CommitInfo = extract.core.CommitInfo;
import User = extract.core.User;

describe('Assign labels', () => {
    it('smoke', () => {
        let testExtract = new Extract(
            "Some",
            "^.*(first).*$",
            "^.*(second).*$",
            [], null, "\${1}", null, null, null, null);

        let label = new ExtractLabel('Some', 'first', null, null, null, null, []);
        let user = new User("dummyUserName", "dummyUserEmail");
        let commit = new CommitInfo(
            "123", [], user, user, "",
            "bla bla bla first",
            "bla bla bla first\\n\\nfoo foo foo first second",
            []
        );

        let resultLabel = extract.core.assignLabel(commit, testExtract);

        expect(label).to.deep.equals(resultLabel);
    });
});