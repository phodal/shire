---
layout: default
title: Hobbit Hole Config
parent: Shire Language
nav_order: 2
---

Hobbit Hole 用于定义数据处理流程与 IDE 交互逻辑。

```shire
---
name: "Summary"
description: "Generate Summary"
interaction: AppendCursor
actionLocation: ContextMenu
---

```

## Hobbit Hole 属性

### Interaction Type

```kotlin
enum class InteractionType(val description: String) {
    AppendCursor("Append content at the current cursor position"),
    AppendCursorStream("Append content at the current cursor position, stream output"),
    OutputFile("Output to a file"),
    ReplaceSelection("Replace the currently selected content"),
    ReplaceCurrentFile("Replace the content of the current file"),
    InsertBeforeSelection("Insert content before the currently selected content"),
    RunPanel("Show Result in Run panel which is the bottom of the IDE")
    ;
}
```

### Action Location

```kotlin
enum class ShireActionLocation(val location: String, val description: String) {
    CONTEXT_MENU("ContextMenu", "Show in Context Menu by Right Click"),
    INTENTION_MENU("IntentionMenu", "Show in Intention Menu by Alt+Enter"),
    TERMINAL_MENU("TerminalMenu", "Show in Terminal panel menu bar"),
    COMMIT_MENU("CommitMenu", "Show in Commit panel menu bar"),
    RUN_PANEL("RunPanel", "Show in Run panel which is the bottom of the IDE")
    ;
}
```

{: .note }
当 COMMIT_MENU 项多于一个时，将会用 PopupMenu 显示；当只有一个时，将直接显示在 Commit 菜单中。

## 示例

### 自动化测试示例

```shire
---
name: "AutoTest"
description: "AutoTest"
interaction: AppendCursor
actionLocation: ContextMenu
when: $fileName.contains(".java") && $filePath.contains("src/main/java")
fileName-rules:
  /.*Controller.java/: "When testing controller, you MUST use MockMvc and test API only."
variables:
  "extContext": /build\.gradle\.kts/ { cat | grep("org.springframework.boot:spring-boot-starter-jdbc") | print("This project use Spring Framework")}
  "testTemplate": /\(.*\).java/ {
    case "$1" {
      "Controller" { cat(".shire/templates/ControllerTest.java") }
      "Service" { cat(".shire/templates/ServiceTest.java") }
      default  { cat(".shire/templates/DefaultTest.java") }
    }
  }
  "allController": {
    from {
        PsiClass clazz /* sample */
    }
    where {
        clazz.getMethods().length() > 0
    }
    select {
        clazz.getMethods()
    }
  }
---
```