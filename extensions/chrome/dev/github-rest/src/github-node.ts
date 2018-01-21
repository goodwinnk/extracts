///<reference path="../../../src/github.ts"/>
import * as ExtractsGitHub from "../../../src/github"
import * as nodeXMLHttpRequest from 'XMLHttpRequest';

declare var global: any;
global.XMLHttpRequest = nodeXMLHttpRequest.XMLHttpRequest;

ExtractsGitHub.fetchCommitData("JetBrains", "kotlin", "2cdc246a274ff3fff92a7c880a905945ed6c0833");
