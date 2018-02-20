import 'mocha';
import {parseExtracts} from "../src/parser";
import {expect} from 'chai';
import {extract} from "core-js";
import Extract = extract.core.Extract;

describe('Extracts yaml parsing', () => {

    let extractWithIcon = `
  - name: YouTrack
    title-pattern: "^.*(KT-\\\\d+).*$"
    icon: path
    text: "\${1}"
    url: "https://youtrack.jetbrains.com/issue/\${1}"
`;
    it("with icon and url", () => {
        checkExtract(extractWithIcon, new Extract(
            "YouTrack",
            "^.*(KT-\\d+).*$",
            null,
            [],
            "path",
            "\${1}",
            "\${1}",
            "https://youtrack.jetbrains.com/issue/\${1}",
            null,
            null)
        );
    });

    let extractWithFiles = `
  - name: IDE
    files: [
      idea/**
    ]
`;
    it("with files", () => {
        checkExtract(extractWithFiles,
            new Extract(
                "IDE",
                null,
                null,
                ["idea/**"],
                null,
                null,
                null,
                null,
                null,
                null)
        );
    });


    let extractWithStyle = `
  - name: Minor
    style: e1
`;
    it("with style", () => {
        checkExtract(extractWithStyle, new Extract(
            "Minor",
            null, null, [], null, null, null, null,
            "e1", null
        ))
    });

    let extractWithBadge = `
  - name: WithBadge
    badge: "\${matched}"
`;
    it("with badge", () => {
        checkExtract(extractWithBadge, new Extract(
            "WithBadge",
            null, null, [], null, null, null, null, null,
            "${matched}"
        ))
    });

    let extractWithMessagePattern = `
  - name: WithMessagePattern
    message-pattern: "^.*(KT-\\\\d+).*$"
`;
    it("with message pattern", () => {
        checkExtract(extractWithMessagePattern, new Extract(
            "WithMessagePattern",
            null, "^.*(KT-\\d+).*$", [],
            null, null, null, null, null,
            null
        ))
    });


    it('simple full example', () => {
        let testFile = `
---
extracts:
${extractWithIcon}
${extractWithFiles}
${extractWithStyle}
${extractWithBadge}
${extractWithMessagePattern}
`;
        let extracts = parseExtracts(testFile);
        expect(extracts.length).to.equal(5);
    });
});

function checkExtract(extractStr, expectedExtract: Extract) {
    let extracts = parseExtracts(toSingleExtractFile(extractStr));
    expect(extracts[0]).to.deep.equals(expectedExtract);
}

function toSingleExtractFile(extractText: string) {
    return `
---
extracts:
${extractText}
`;
}