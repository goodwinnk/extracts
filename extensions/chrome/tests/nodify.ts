///<reference types = "../node_modules/@types/node"/>
import * as nodeXMLHttpRequest from "XMLHttpRequest";
declare var global: any;

// noinspection SpellCheckingInspection
export function nodify() {
    if (typeof XMLHttpRequest === 'undefined') {
        global.XMLHttpRequest = nodeXMLHttpRequest.XMLHttpRequest;
    }

    if (typeof btoa === 'undefined') {
        global.btoa = function (str) {
            return new Buffer(str, 'binary').toString('base64');
        };
    }

    if (typeof atob === 'undefined') {
        global.atob = function (b64Encoded) {
            return new Buffer(b64Encoded, 'base64').toString('binary');
        };
    }
}