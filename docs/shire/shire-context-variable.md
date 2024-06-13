---
layout: default
title: Shire Context Variable
parent: Shire Language
nav_order: 4
---

Context Variables for All

```kotlin
enum class ContextVariable(val variable: String, val description: String) {
    SELECTION("selection", "The selected text"),
    BEFORE_CURSOR("beforeCursor", "The text before the cursor"),
    AFTER_CURSOR("afterCursor", "The text after the cursor"),
    FILE_NAME("fileName", "The name of the file"),
    FILE_PATH("filePath", "The path of the file"),
    METHOD_NAME("methodName", "The name of the method"),
    LANGUAGE("language", "The language of the file"),
    COMMENT_SYMBOL("commentSymbol", "The comment symbol of the language"),
    FRAMEWORK_CONTEXT("frameworkContext", "The context of the framework"),
    ALL("all", "All the text")
    ;
}
```

