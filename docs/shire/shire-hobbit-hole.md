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
enum class InteractionType {
    ChatPanel,          // 输出结果到聊天 Panel
    AppendCursor,       // 在当前光标位置追加内容
    AppendCursorStream, // 在当前光标位置追加内容，以流式输出
    OutputFile,         // 输出到文件
    ReplaceSelection,   // 替换当前选中内容
    ReplaceCurrentFile, // 替换当前文件的内容
    ;
}
```

### Action Location

```kotlin
enum class ShireActionLocation(val location: String) {
    CONTEXT_MENU("ContextMenu"),      // 在鼠标右键菜单中
    INTENTION_MENU("IntentionMenu"),  // 在 Intention 菜单中，通过 Alt+Enter 快捷键触发
    TERMINAL_MENU("TerminalMenu"),    // 在 Terminal 菜单中
    COMMIT_MENU("CommitMenu"),        // 在 Commit 菜单中
    RunPanel("RunPanel")              // 在 Run 面板中
    ;
}
```



## 示例

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