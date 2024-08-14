---
layout: default
title: Shire Toolchain Variable
parent: Shire Language
nav_order: 4
---

# Toolchain Variable

工具链变量提供诸如语言、框架和其他工具等数据作为变量。这个数据可以在 Shire 变量和模板中使用。

支持的工具链：

- Git
- SonarQube
- 依赖：Maven, Gradle （待支持）

## Git

Git 工具链提供以下变量：

- `currentChanges`，当前分支的当前更改。
- `currentBranch`，当前分支的名称。
- `historyCommitMessages`，当前提交的提交消息。

## SonarQube

SonarQube 工具链提供以下变量：

- `sonarIssues`，当前文件的 SonarQube 中的问题列表。
- `sonarResults`，当前文件的 SonarQube 中的结果，含有问题的详细信息。
