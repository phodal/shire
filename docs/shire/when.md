---
layout: default
title: Condition Visible
parent: Shire Language
nav_order: 9
---

Activate Menu 是一类 `Condition Visible` 用于定义一个条件，当条件为真时，将显示对应的 Action。

当前支持 Action 类型：ContextMenu（右键）和 Intention Action（意图感知，通过 Alt+Enter）。

## 语法示例

```shire
---
when: { $filePath.contains("src/main/java") && $fileName.contains(".java") }
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
    - `filePath`：文件路径
    - `fileName`：文件名
    - `fileExtension`：文件扩展名
    - `fileContent`：文件内容

### 支持的操作符

- `==`：等于
- `!=`：不等于
- `>`：大于
- `<`：小于
- `>=`：大于等于
- `<=`：小于等于

### 支持的函数

详细见：[com.phodal.shirelang.compiler.hobbit.ast.ExpressionBuiltInMethod]

```Kotlin
enum class ExpressionBuiltInMethod(
    val methodName: String,
    val description: String,
    val postInsertString: String = "()",
    val moveCaret: Int = 2,
) {
    LENGTH("length", "The length of the string"),
    TRIM("trim", "The trimmed string"),
    CONTAINS("contains", "Check if the string contains a substring", "(\"\")", 2),
    STARTS_WITH("startsWith", "Check if the string starts with a substring", "(\"\")", 2),
    ENDS_WITH("endsWith", "Check if the string ends with a substring", "(\"\")", 2),
    LOWERCASE("lowercase", "The lowercase version of the string"),
    UPPERCASE("uppercase", "The uppercase version of the string"),
    IS_EMPTY("isEmpty", "Check if the string is empty"),
    IS_NOT_EMPTY("isNotEmpty", "Check if the string is not empty"),
    FIRST("first", "The first character of the string"),
    LAST("last", "The last character of the string"),
    MATCHES("matches", "Check if the string matches a regex pattern", "(\"//\")", 3);
}
```
