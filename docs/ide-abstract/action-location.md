---
layout: default
title: Action Location
parent: IDE Abstract
nav_order: 2
---

## Action Location

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

