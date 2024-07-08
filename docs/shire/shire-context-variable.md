---
layout: default
title: Shire Context Variable
parent: Shire Language
nav_order: 5
---

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

| 变量名                 | 描述                                                                          |
|---------------------|-----------------------------------------------------------------------------|
| currentClassName    | The name of the current class                                               |
| currentClassCode    | The code of the current class                                               |
| currentMethodName   | The name of the current method                                              |
| currentMethodCode   | The code of the current method                                              |
| relatedClasses      | The related classes based on the AST analysis                               |
| similarTestCase     | The similar test cases based on the TfIDF analysis                          |
| imports             | The import statements required for the code structure                       |
| isNeedCreateFile    | Flag indicating whether the code structure is being generated in a new file |
| targetTestFileName  | The name of the target test file where the code structure will be generated |
| underTestMethodCode | The code of the method under test                                           |
| frameworkContext    | The framework information in dependencies of current project                |
| codeSmell           | Include psi error and warning                                               |
| methodCaller        | The method that initiates the current call                                  |
| calledMethod        | The method that is being called by the current method                       |
| similarCode         | Recently 20 files similar code based on the tf-idf search                   |

