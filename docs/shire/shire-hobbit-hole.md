---
layout: default
title: Shire Config (Hobbit Hole)
parent: Shire Language
nav_order: 3
---

Hobbit Hole 用于定义数据处理流程与 IDE 交互逻辑。

## Hobbit Hole 概述

```shire
---
name: "Summary"
description: "Generate Summary"
interaction: AppendCursor
actionLocation: ContextMenu
---
```

### 属性说明

- `name`：Shire 命令的显示名称，将显示在 IDE 的 UI 中，基于 [HobbitHole.interaction](#interaction)。
- `description`：操作的提示信息，将显示在 UI 的 Hover tips 中。
- `interaction`：操作的输出可以是编辑器中的流文本，当使用 [InteractionType.AppendCursorStream](#interaction) 时。
- `actionLocation`：操作的位置，应该是 [ShireActionLocation](#actionlocation) 中的一个，默认为 [ShireActionLocation.RUN_PANEL]。
- `selectionStrategy`：选择要应用操作的元素的策略。
- `variables`：用于构建变量的带有 PatternAction 的变量列表。
- `when`：用于 [com.intellij.codeInsight.intention.IntentionAction.isAvailable]、[com.intellij.openapi.project.DumbAwareAction.DumbAwareAction.update] 检查是否显示菜单的条件。
- `ruleBasedFilter`：应用于操作的规则文件列表。
- `onStreamingEnd`：在流处理结束后执行的后中间件操作列表，如日志记录、指标收集、代码验证、运行代码、解析代码等。
- `afterStreaming`：流结束后执行任务的决策，路由到不同的任务。
- `shortcut`：操作的 IDE 快捷键，使用 IntelliJ IDEA 的快捷键格式。
- `userData`：其余数据。

### 示例代码

```shire
---
name: "Summary"
description: "Generate Summary"
interaction: AppendCursor
actionLocation: ContextMenu
when: $fileName.matches("/.*.java/")
variables:
  "var1": "demo"
  "var2": /**.java/ { find("error.log") | sort | xargs("rm")}
---
```

## Hobbit Hole 属性

### Interaction Type

```kotlin
enum class InteractionType(val description: String) {
    AppendCursor("Append content at the current cursor position"),
    AppendCursorStream("Append content at the current cursor position, stream output"),
    OutputFile("Output to a new file"),
    ReplaceSelection("Replace the currently selected content"),
    ReplaceCurrentFile("Replace the content of the current file"),
    InsertBeforeSelection("Insert content before the currently selected content"),
    RunPanel("Show Result in Run panel which is the bottom of the IDE"),
    OnPaste("Copy the content to the clipboard")
    ;
}
```

{: .note }
由于性能原因，OnPaste 暂时只支持 Java 和 Kotlin 语言，并且需要行数多于 5 行。

### Action Location

```kotlin
enum class ShireActionLocation(val location: String, val description: String) {
    CONTEXT_MENU("ContextMenu", "Show in Context Menu by Right Click"),
    INTENTION_MENU("IntentionMenu", "Show in Intention Menu by Alt+Enter"),
    TERMINAL_MENU("TerminalMenu", "Show in Terminal panel menu bar"),
    COMMIT_MENU("CommitMenu", "Show in Commit panel menu bar"),
    RUN_PANEL("RunPanel", "Show in Run panel which is the bottom of the IDE"),
    INPUT_BOX("InputBox", "Show in Input Box")
    ;
}
```

{: .note }
当 COMMIT_MENU 项多于一个时，将会用 PopupMenu 显示；当只有一个时，将直接显示在 Commit 菜单中。
