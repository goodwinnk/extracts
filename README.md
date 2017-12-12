[![Build Status](https://travis-ci.org/goodwinnk/extract.svg?branch=master)](https://travis-ci.org/goodwinnk/extract)

# Commit Extracts

## Motivation

Git considers repository as a mere bunch of files and folders and does not know anything 
about project structure, modifications in "images/logo.png", "core/Engine.java" or ".gitignore" 
files would be effectively same to Git. As there might be a lot of files changed in a commit,
tools for history usually show only author, date and message in the list, that makes reading 
commit messages one by one the main option when any investigation needed. 

With Extracts project you can automate collecting an important information for your project 
from commits and show it as a list of labels in the history. It might be sub-projects or 
modules names, links to issues in your tracker, or even particular files highlights. This will 
make the log far more useful and easier to work with.

![Extracts](docs/Concept.png?raw=true "Extracts")

### Install

Download and unpack the latest release from the [releases](https://github.com/goodwinnk/extract/releases).

### Run

```
bin/extracts -r {path to repository with .extracts file}
```

### CLI arguments

```
-extracts (-f) FILE : Path to extracts file. "repository/.extracts" path is
                      used by default.
-help               : Get the program options help
-html-log FILE      : Output html file. System temporary directory is used by
                      default.
-number (-n) N      : Limit the number of commits to output. Default is 50.
-open               : Is result history should be open automatically. Default
                      is TRUE.
-revision VAL       : Revision where log should start. HEAD commit is used by
                      default.
```

## Extracts Setup

Check [example](/demo/src/main/resources/) yaml files.

### Examples

- A project tracker link for the issue mentioned anywhere in the commit message

```
  - name: YouTrack
    message-pattern: "^.*(KT-\\d+).*$"
    text: "${1}"
    url: "https://youtrack.jetbrains.com/issue/${1}"
    style: e6
```

- Show the number of affected Java files in the commit

```
  - name: .java
    badge: "${matches}/${count}"
    files: [
      "**.java"
    ]
```

- Highlight changes in some subsystem

```
  - name: JS
    files: [
      js/**
    ]
```

- Highlight a special file affected by the commit

```
  - name: ChangeLog
    files: [
      ChangeLog.md
    ]
```
