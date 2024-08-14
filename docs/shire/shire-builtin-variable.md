---
layout: default
title: Shire Builtin Variable
parent: Shire Language
nav_order: 3
---

在 Shire 中，我们提供了一些内置变量，以便用户可以更方便地使用。

当前支持的内置变量有：

- [ContextVariable](#contextvariable) 用于提供当前文件的上下文信息。
- [PsiContextVariable](#psicontextvariable) 用于提供当前 AST/PSI 语法树的上下文信息。
- [Toolchain Variable](#toolchain-variable) 用于提供项目的工具链信息。

## ContextVariable

| 变量名           | 描述                                    |
|---------------|---------------------------------------|
| selection     | User selection code/element's in text |
| beforeCursor  | All the text before the cursor        |
| afterCursor   | All the text after the cursor         |
| fileName      | The name of the file                  |
| filePath      | The path of the file                  |
| methodName    | The name of the method                |
| language      | The language of the current file      |
| commentSymbol | The comment symbol of the language    |
| all           | All the text                          |

## PsiContextVariable

| 变量名                 | 描述                                                                                 |
|---------------------|------------------------------------------------------------------------------------|
| currentClassName    | The name of the current class                                                      |
| currentClassCode    | The code of the current class                                                      |
| currentMethodName   | The name of the current method                                                     |
| currentMethodCode   | The code of the current method                                                     |
| relatedClasses      | The related classes based on the AST analysis                                      |
| similarTestCase     | The similar test cases based on the TfIDF analysis                                 |
| imports             | The import statements required for the code structure                              |
| isNeedCreateFile    | Flag indicating whether the code structure is being generated in a new file        |
| targetTestFileName  | The name of the target test file where the code structure will be generated        |
| underTestMethodCode | The code of the method under test                                                  |
| frameworkContext    | The framework information in dependencies of current project                       |
| codeSmell           | Include psi error and warning                                                      |
| methodCaller        | The method that initiates the current call                                         |
| calledMethod        | The method that is being called by the current method                              |
| similarCode         | Recently 20 files similar code based on the tf-idf search                          |
| structure           | The structure of the current class, for programming language will be in UML format |

## Toolchain Variable

工具链变量提供诸如语言、框架和其他工具等数据作为变量。这个数据可以在 Shire 变量和模板中使用。

支持的工具链：

- Git
- SonarQube
- 依赖：Maven, Gradle （待支持）

### Git

Git 工具链提供以下变量：

- `currentChanges`，当前分支的当前更改。
- `currentBranch`，当前分支的名称。
- `historyCommitMessages`，当前提交的提交消息。

### SonarQube

SonarQube 工具链提供以下变量：

- `sonarIssues`，当前文件的 SonarQube 中的问题列表。
- `sonarResults`，当前文件的 SonarQube 中的结果，含有问题的详细信息。
