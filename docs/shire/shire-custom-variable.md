---
layout: default
title: Shire Custom Variable
parent: Shire Language
nav_order: 4
---

为了让用户更好地使用 Shire，我们提供了一些常规自定义变量，以便用户可以更方便地使用。

几种方式：

```shire
---
variables:
  "var1": "demo"
  "var2": /.*.java/ { grep("error.log") | sort | xargs("rm")}
  "var3": /.*.log/ {
    case "$0" {
      "error" { grep("ERROR") | sort | xargs("notify_admin") }
      "warn" { grep("WARN") | sort | xargs("notify_admin") }
      "info" { grep("INFO") | sort | xargs("notify_user") }
      default  { grep("ERROR") | sort | xargs("notify_admin") }
    }
  }  
---
```

## Shire 常规自定义变量

常规自定义变量，可以用于：

- 对应不同类型文件，自定义 prompt。

## Variable Pattern Action

在Shire中，我们借鉴了 Unix/Linux 的设计理念和 Shell 编程模式，特别是 Pattern-Action 模型。该模型通过定义模式和动作来处理数据：

1. **模式（Pattern）**：这代表用于筛选输入数据的规则或标准。在Unix/Linux中，这可以是一组文件名模式、正则表达式或其他条件，用于识别哪些数据需要进一步处理。
2. **动作（Action）**：这是当数据符合模式时需要执行的任务。它由一系列命令组成，描述了如何处理匹配的数据。

例如，在Shire中，您可以编写如下代码：

```text
/*.java/ { grep("error.log") | sort | print }
```

这里，`/*.java/` 是模式部分，用于匹配所有以 `.java` 结尾的文件，而 `{ grep("error.log") | sort | xargs("rm")}` 是动作部分，
表示对匹配的文件执行一系列操作：首先搜索包含 "error.log" 的行，然后对这些行进行排序，最后将结果输出到标准输出。

在 Shire 中，我们利用了 Intellij 的强大功能，如正则表达式匹配、代码高亮和语法检查，以帮助用户更高效地编写代码。例如，
使用正则表达式 `.*.java` 可以轻松地匹配所有 Java 源文件。

明白了！让我为您完整优化一下文档，包括对示例的详细解释：

### 示例 1：Pattern-Action Pipeline

```shire
---
variables:
  "var2": /.*.java/ { cat | grep("error.log") | sort | cat }
  "extContext": /build\.gradle\.kts/ { cat | grep("org.springframework.boot:spring-boot-starter-jdbc") | print("This project use Spring Framework") }
---
```

在这个示例中：

- **`var2` 变量**：匹配所有以 `.java` 结尾的文件。动作部分使用了管道操作符 `|`，依次执行了 `grep("error.log")`、`sort`
  ，然后再次使用 `cat` 输出结果。

- **`extContext` 变量**：匹配所有名为 `build.gradle.kts`
  的文件。动作部分执行了 `grep("org.springframework.boot:spring-boot-starter-jdbc")`，并输出一条指示该项目使用 Spring
  Framework 的信息。

### 示例 2：Pattern-Action 多 CASE

```shire
---
variables:
  "testTemplate": /\(.*\).java/ {
    case "$1" {
      "Controller" { cat(".shire/templates/ControllerTest.java") }
      "Service" { cat(".shire/templates/ServiceTest.java") }
      default  { cat(".shire/templates/DefaultTest.java") }
    }
  }
---
```

在这个示例中：

- **`testTemplate` 变量**：匹配所有以 `(.*)` 开头、`.java` 结尾的文件。根据不同的匹配结果执行不同的动作。
    - 如果匹配到 `Controller`，则输出 `ControllerTest.java` 的内容。
    - 如果匹配到 `Service`，则输出 `ServiceTest.java` 的内容。
    - 如果没有匹配到上述任何值（`default`），则输出 `DefaultTest.java` 的内容。

## Pattern Function

| 函数类别      | 功能描述        | 参数                                                                 | 示例                                          |
|-----------|-------------|--------------------------------------------------------------------|---------------------------------------------|
| prompt    | 显示消息提示      | `message`: 要显示的消息内容                                                | `prompt("You are xxx")`                     |
| grep      | 使用模式进行搜索    | `patterns`: 要搜索的模式                                                 | `grep("error")`                             |
| sed       | 查找和替换操作     | `pattern`: 要查找的模式<br>`replacements`: 替换的字符串<br>`isRegex`: 是否为正则表达式 | `sed("s/old/new/g")`                        |
| sort      | 排序操作        | `arguments`: 排序所需的参数                                               | `sort`                                      |
| uniq      | 去除重复行       | `texts`: 要处理的文本                                                    | `uniq("line1", "line2", "line1")`           |
| head      | 获取文件的前几行    | `number`: 要获取的行数                                                   | `head(10)`                                  |
| tail      | 获取文件的末尾几行   | `number`: 要获取的行数                                                   | `tail(5)`                                   |
| xargs     | 处理变量        | `variables`: 要处理的变量                                                | `xargs("arg1", "arg2")`                     |
| print     | 打印文本        | `texts`: 要打印的文本                                                    | `print("Hello", "World")`                   |
| cat       | 连接文件        | `paths`: 要连接的文件路径                                                  | `cat("file1.txt", "file2.txt")`             |
| execute   | 执行 Shire 脚本 | `string`: 要执行的脚本内容                                                 | `execute("next-script.shire")`              |
| notify    | 使用 IDE 通知   | `message`: 要显示的通知消息                                                | `notify("Process completed successfully.")` |
| splitting | 分割文本或文件     | `paths`: 要分割的文本或文件路径                                               | `splitting("file.txt", "file2.txt")`        |
| embedding | 嵌入文本        | `entries`: 要嵌入的文本条目                                                | `embedding("entry1", "entry2")`             |
| searching | 搜索文本        | `text`: 要搜索的文本                                                     | `searching("pattern")`                      |
| reranking | 重新排序        | `type`: 重排类型，默认  lostInTheMiddle                                   | `reranking("pattern")`                      |
| caching   | 缓存语义        | `text`: 要缓存的文本                                                     | `caching("data")`                           |
| redact    | 屏蔽敏感数据      |                                                                    | `redact()`                                  |

```kotlin
sealed class PatternActionFunc(open val funcName: String) {
    /**
     * Prompt subclass for displaying a message prompt.
     *
     * @property message The message to be displayed.
     */
    class Prompt(val message: String) : PatternActionFunc("prompt")

    /**
     * Grep subclass for searching with one or more patterns.
     *
     * @property patterns The patterns to search for.
     */
    class Grep(vararg val patterns: String) : PatternActionFunc("grep")

    /**
     * Sed subclass for find and replace operations.
     *
     * @property pattern The pattern to search for.
     * @property replacements The string to replace matches with.
     */
    class Sed(val pattern: String, val replacements: String, val isRegex: Boolean = true) : PatternActionFunc("sed")

    /**
     * Sort subclass for sorting with one or more arguments.
     *
     * @property arguments The arguments to use for sorting.
     */
    class Sort(vararg val arguments: String) : PatternActionFunc("sort")

    /**
     * Uniq subclass for removing duplicates based on one or more arguments.
     *
     * @property texts The texts to process for uniqueness.
     */
    class Uniq(vararg val texts: String) : PatternActionFunc("uniq")

    /**
     * Head subclass for retrieving the first few lines.
     *
     * @property number The number of lines to retrieve from the start.
     */
    class Head(val number: Number) : PatternActionFunc("head")

    /**
     * Tail subclass for retrieving the last few lines.
     *
     * @property number The number of lines to retrieve from the end.
     */
    class Tail(val number: Number) : PatternActionFunc("tail")

    /**
     * Xargs subclass for processing one or more variables.
     *
     * @property variables The variables to process.
     */
    class Xargs(vararg val variables: String) : PatternActionFunc("xargs")

    /**
     * Print subclass for printing one or more texts.
     *
     * @property texts The texts to be printed.
     */
    class Print(vararg val texts: String) : PatternActionFunc("print")

    /**
     * Cat subclass for concatenating one or more files.
     * Paths can be absolute or relative to the current working directory.
     */
    class Cat(vararg val paths: String) : PatternActionFunc("cat")

    /**
     * Select subclass for selecting one or more elements.
     */
    class From(val variables: List<VariableElement>) : PatternActionFunc("from")

    /**
     * Where subclass for filtering elements.
     */
    class Where(val statement: Statement) : PatternActionFunc("where")

    /**
     * OrderBy subclass for ordering elements.
     */
    class Select(val statements: List<Statement>) : PatternActionFunc("select")

    /**
     * Execute a shire script
     */
    class ExecuteShire(val string: String) : PatternActionFunc("execute")

    /**
     * use IDE Notify
     */
    class Notify(val message: String) : PatternActionFunc("notify")

    /**
     * Case Match
     */
    class CaseMatch(val keyValue: List<CaseKeyValue>) : PatternActionFunc("switch")

    /**
     * Splitting
     */
    class Splitting(val paths: Array<String>) : PatternActionFunc("splitting")

    /**
     * Embedding text
     */
    class Embedding(val entries: Array<String>) : PatternActionFunc("embedding")

    /**
     * searching text
     */
    class Searching(val text: String) : PatternActionFunc("searching")

    /**
     * Caching semantic
     */
    class Caching(val text: String) : PatternActionFunc("caching")

    /**
     * User Custom Functions
     */
    class UserCustom(override val funcName: String, val args: List<String>) : PatternActionFunc(funcName) {
        override fun toString(): String {
            return "$funcName(${args.joinToString(", ")})"
        }
    }
}
```
