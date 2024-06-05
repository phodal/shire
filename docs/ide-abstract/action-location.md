---
layout: default
title: Action Location
parent: IDE Abstract
nav_order: 2
---

## Action Location

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

