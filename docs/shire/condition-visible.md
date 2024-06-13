---
layout: default
title: Condition Visible
parent: Shire Language
nav_order: 5
---

`Condition Visible` 用于定义一个条件，当条件为真时，将显示对应的 Action。

当前支持 Action 类型：ContextMenu（右键）和 Intention Action（意图感知，通过 Alt+Enter）。

## 语法示例

```shire
---
when: $filePath.contains("src/main/java") && $fileName.contains(".java")
---
```

当当前文件路径包含 `src/main/java` 且文件名包含 `.java` 时，显示对应的 Action。

## 语法说明

- `when`：条件表达式，当条件为真时，显示对应的 Action。
- `$`：变量引用符号。
- `.`：属性访问符号。
- `&&`：逻辑与。
- `contains`：字符串包含。

### 支持的变量

- PsiVariables
    - filePath
    - fileName
    - fileType
    - fileExtension
    - fileContent

### 支持的操作符

- `==`：等于
- `!=`：不等于
- `>`：大于
- `<`：小于
- `>=`：大于等于
- `<=`：小于等于

### 支持的函数

详细见：[com.phodal.shirelang.compiler.hobbit.MethodCall]

- "length" -> value.length
- "trim" -> value.trim()
- "contains" -> value.contains(parameters[0] as String)
- "startsWith" -> value.startsWith(parameters[0] as String)
- "endsWith" -> value.endsWith(parameters[0] as String)
- "lowercase" -> value.lowercase()
- "uppercase" -> value.uppercase()
- "isEmpty" -> value.isEmpty()
- "isNotEmpty" -> value.isNotEmpty()
- "first" -> value.first().toString()
- "last" -> value.last().toString()
