---
layout: default
title: Interaction Type
parent: IDE Abstract
nav_order: 3
---

```
---
name: "Summary"
description: "Generate Summary"
interaction: AppendCursor
actionLocation: ContextMenu
---


```

## Interaction Type

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
