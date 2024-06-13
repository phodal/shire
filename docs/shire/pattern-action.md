---
layout: default
title: Pattern Action
parent: Shire Language
nav_order: 4
---

我们参考 Unix 的 Shell 编程，引入了 Pattern-Action 的概念。Pattern-Action 是一种编程结构，用于根据特定条件（模式）执行相应操作的机制。它通常由两个部分组成：

1. **Pattern（模式）**：匹配输入数据的规则或条件。模式可以是正则表达式、特定值、范围或逻辑表达式，用于确定哪些输入数据满足条件。
2. **Action（动作）**：当输入数据匹配模式时要执行的操作。动作是由一组命令或代码组成，定义了对匹配数据的处理方式。

简单表达如下：

```plaintext
/*.java/ { grep("error.log") | sort | xargs("rm")}
```

其中的 `/*.java/` 是模式（Pattern），`{ grep("error.log") | sort | xargs("rm")}` 是动作（Action）。

## Pattern Function

```kotlin
sealed class PatternFun(open val funcName: String) {
    /**
     * Prompt subclass for displaying a message prompt.
     *
     * @property message The message to be displayed.
     */
    class Prompt(val message: String) : PatternFun("prompt")

    /**
     * Grep subclass for searching with one or more patterns.
     *
     * @property patterns The patterns to search for.
     */
    class Grep(vararg val patterns: String) : PatternFun("grep")

    /**
     * Sed subclass for find and replace operations.
     *
     * @property pattern The pattern to search for.
     * @property replacements The string to replace matches with.
     */
    class Sed(val pattern: String, val replacements: String) : PatternFun("sed")

    /**
     * Sort subclass for sorting with one or more arguments.
     *
     * @property arguments The arguments to use for sorting.
     */
    class Sort(vararg val arguments: String) : PatternFun("sort")

    /**
     * Uniq subclass for removing duplicates based on one or more arguments.
     *
     * @property texts The texts to process for uniqueness.
     */
    class Uniq(vararg val texts: String) : PatternFun("uniq")

    /**
     * Head subclass for retrieving the first few lines.
     *
     * @property lines The number of lines to retrieve from the start.
     */
    class Head(val lines: Number) : PatternFun("head")

    /**
     * Tail subclass for retrieving the last few lines.
     *
     * @property lines The number of lines to retrieve from the end.
     */
    class Tail(val lines: Number) : PatternFun("tail")

    /**
     * Xargs subclass for processing one or more variables.
     *
     * @property variables The variables to process.
     */
    class Xargs(vararg val variables: String) : PatternFun("xargs")

    /**
     * Print subclass for printing one or more texts.
     *
     * @property texts The texts to be printed.
     */
    class Print(vararg val texts: String) : PatternFun("print")

    /**
     * Cat subclass for concatenating one or more files.
     * Paths can be absolute or relative to the current working directory.
     */
    class Cat(vararg val paths: String) : PatternFun("cat")
}
```

### Shire 示例

```shire
---
variables:
  "var1": "value2"
  "var2": /.*.java/ { grep("error.log") | sort | xargs("rm")}
  "var3": /.*.java/ {
    case "$0" {
      "error" { grep("ERROR") | sort | xargs("notify_admin") }
      "warn" { grep("WARN") | sort | xargs("notify_admin") }
      "info" { grep("INFO") | sort | xargs("notify_user") }
      default  { grep("ERROR") | sort | xargs("notify_admin") }
    }
  }
---


```

Shire 使用 Intellij 自带的正则表达式来匹配：

```regexp
.*.java
```
