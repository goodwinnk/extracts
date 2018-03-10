import * as YamlParser from "js-yaml";
import * as CoreJs from "core-js";
import Extract = CoreJs.extract.core.Extract;

export function parseExtracts(text: string): Array<Extract> {
    let documents = YamlParser.loadAll(text) as Array<any>;
    if (documents.length == 0) {
        return [];
    }

    let parsed = documents[0];
    if (parsed.extracts === undefined) {
        return [];
    }

    let rawExtracts: Array<any> = documents[0].extracts;

    let result: Array<Extract> = [];
    for (let rawExtract of rawExtracts) {
        let extract = new Extract(
            rawExtract.name,
            rawExtract['title-pattern'],
            rawExtract['message-pattern'],
            rawExtract.files,
            rawExtract.icon,
            rawExtract.text,
            rawExtract.hint ? rawExtract.hint : rawExtract.text,
            rawExtract.url,
            rawExtract.style,
            rawExtract.badge);

        result.push(extract);
    }

    return result;
}