---
layout: default
title: Shire Toolchain Variable
parent: Shire Language
nav_order: 7
---

The toolchain variable supplies data such as language, framework, and other tools as a variable. This can be utilized in
the Shire Variable and Template.

Supported toolchain:

- Git
- Maven, Gradle

## Git

Git toolchain provides the following variables:

- `currentChanges`, which is the current changes in the current branch.
- `currentBranch`, which is the current branch name.
- `historyCommitMessage`, which is the commit message of the current commit.
