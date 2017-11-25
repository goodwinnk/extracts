# Commit Extracts

![Extracts](docs/Concept.png?raw=true "Extracts")

## Console Interface

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

- Highlight a special file is affected in the commit

```
  - name: JS
    files: [
      js/**
    ]
```
