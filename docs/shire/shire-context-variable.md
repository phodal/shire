---
layout: default
title: Shire Context Variable
parent: Shire Language
nav_order: 5
---

使用方式：

```shire
直接在代码中使用 $selection 即可。
```

## Basic Context Variables

| 变量名           | 类型                                                                |
|---------------|-------------------------------------------------------------------|
| selection     | User selection code/element's in text                             |
| beforecursor  | All the text before the cursor                                    |
| aftercursor   | All the text after the cursor                                     |
| filename      | The name of the file                                              |
| filepath      | The path of the file                                              |
| methodname    | The name of the method                                            |
| language      | The language of the current file, will use IntelliJ's language ID |
| commentsymbol | The comment symbol of the language, for example, `//` in Java     |
| all           | All the text                                                      |

## PSI Context Variables

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