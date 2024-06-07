---
layout: default
title: IDE Abstract
nav_order: 4
has_children: true
permalink: /ide-abstract
---

# IDE 抽象接口

```shire
---
name: "Summary"
description: "Generate Summary"
interaction: AppendCursor
actionLocation: ContextMenu
selectionStrategy: Block
---
```

## Selection Strategy

```kotlin
fun fromString(strategy: String): SelectElementStrategy {
    return when (strategy.lowercase()) {
        "block" -> Blocked
        "select" -> Selected
        "all" -> All
        else -> Default
    }
}
```
