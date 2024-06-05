---
layout: default
title: IDE Abstract
nav_order: 2
has_children: true
---

# IDE 抽象接口

## 类型定义

### ShireActionLocation

```kotlin
enum class ShireActionLocation(val location: String) {
    CONTEXT_MENU("ContextMenu"),
    INTENTION_MENU("IntentionMenu"),
    TERMINAL_MENU("TerminalMenu"),
    COMMIT_MENU("CommitMenu"),
    RunPanel("RunPanel")
    ;
}
```

### InteractionType

```
enum class InteractionType {
    ChatPanel,
    AppendCursor,
    AppendCursorStream,
    OutputFile,
    ReplaceSelection,
    ReplaceCurrentFile,
    ;
}
```
