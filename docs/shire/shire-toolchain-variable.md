---
layout: default
title: Shire Toolchain Variable
parent: Shire Language
nav_order: 6
---

# Toolchain Variable

工具链变量提供诸如语言、框架和其他工具等数据作为变量。这个数据可以在 Shire 变量和模板中使用。

支持的工具链：

- Git
- SonarQube
- Maven, Gradle（TODO）

## Git

{: .label .label-red }
Builtin plugin: Git4Idea

Git 工具链提供以下变量：

- `currentChanges`，当前分支的当前更改。
- `currentBranch`，当前分支的名称。
- `historyCommitMessages`，当前提交的提交消息。
- `diff`，当前选中 commit 的 diff 信息。

## SonarQube

{: .label .label-red }
Require: [Sonarlint plugin](https://plugins.jetbrains.com/plugin/7973-sonarlint)

SonarQube 工具链提供以下变量：

- `sonarIssues`，当前文件的 SonarQube 中的问题列表。
- `sonarResults`，当前文件的 SonarQube 中的结果，含有问题的详细信息。

## ProjectDependencies (TBC)

{: .label .label-red }
Builtin plugin: Java

Maven, Gradle 工具链提供以下变量：

- `projectDependencies`，当前文件的 Maven, Gradle 依赖列表。

## Database

{: .label .label-red }
Builtin plugin: Database

数据库工具链提供以下变量：

- `databases`，当前连接的数据库列表。
- `tables`，当前数据库的表列表。
- `columns`，当前数据库的表的列列表。
