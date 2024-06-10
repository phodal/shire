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

