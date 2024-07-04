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

## Keyboard Binding


```kotlin
object KeyBindingsMappings {
  val defaultVSMacKeymap: Map<String, List<KeyboardShortcut>> = mapOf<String, List<KeyboardShortcut>>(
    "EditorBackSpace" to listOf(KeyboardShortcut.fromString("pressed BACK_SPACE")),
    "\$Copy" to listOf(KeyboardShortcut.fromString("meta pressed C")),
    "\$Cut" to listOf(KeyboardShortcut.fromString("meta pressed X")),
    "\$Paste" to listOf(KeyboardShortcut.fromString("meta pressed V")),
    "EditorDelete" to listOf(KeyboardShortcut.fromString("meta pressed BACK_SPACE")),
    "RenameElement" to listOf(KeyboardShortcut.fromString("meta pressed R"), KeyboardShortcut.fromString("pressed F2")),
    "\$Undo" to listOf(KeyboardShortcut.fromString("meta pressed Z")),
    "\$Redo" to listOf(KeyboardShortcut.fromString("shift meta pressed Z")),
    "\$SelectAll" to listOf(KeyboardShortcut.fromString("meta pressed A")),
    "CommentByLineComment" to listOf(KeyboardShortcut.fromString("meta pressed SLASH")),
    "EditorIndentSelection" to listOf(KeyboardShortcut.fromString("meta pressed CLOSE_BRACKET")),
    "EditorUnindentSelection" to listOf(KeyboardShortcut.fromString("meta pressed OPEN_BRACKET")),
    "ShowSettings" to listOf(KeyboardShortcut.fromString("meta pressed COMMA")),
    "BuildSolutionAction" to listOf(KeyboardShortcut.fromString("meta pressed B"), KeyboardShortcut.fromString("pressed F6")),
    "RebuildSolutionAction" to listOf(KeyboardShortcut.fromString("ctrl meta pressed B")),
    "Run" to listOf(KeyboardShortcut.fromString("meta alt pressed ENTER"), KeyboardShortcut.fromString("ctrl pressed F5")),
    "Stop" to listOf(KeyboardShortcut.fromString("shift meta pressed ENTER"), KeyboardShortcut.fromString("shift pressed F5")),
    "Debug" to listOf(KeyboardShortcut.fromString("meta pressed ENTER"), KeyboardShortcut.fromString("pressed F5")),
    "RiderOpenSolution" to listOf(KeyboardShortcut.fromString("meta pressed O")),
    "FileChooser.NewFile" to listOf(KeyboardShortcut.fromString("meta pressed N")),
    "SaveDocument" to listOf(KeyboardShortcut.fromString("meta pressed S")),
    "SaveAll" to listOf(KeyboardShortcut.fromString("meta alt pressed S")),
    "RiderNewSolution" to listOf(KeyboardShortcut.fromString("shift meta pressed N")),
    "CloseContent" to listOf(KeyboardShortcut.fromString("meta pressed W")),
    "CloseAllEditors" to listOf(KeyboardShortcut.fromString("shift meta pressed W")),
    "CloseProject" to listOf(KeyboardShortcut.fromString("meta alt pressed W")),
    "Exit" to listOf(KeyboardShortcut.fromString("meta pressed Q")),
    "PinActiveTab" to listOf(KeyboardShortcut.fromString("meta alt pressed P")),
    "ToggleFullScreen" to listOf(KeyboardShortcut.fromString("ctrl meta pressed F")),
    "MoveTabRight" to listOf(KeyboardShortcut.fromString("ctrl meta pressed RIGHT")),
    "EditorIncreaseFontSize" to listOf(KeyboardShortcut.fromString("meta pressed PLUS"),
                                       KeyboardShortcut.fromString("meta pressed EQUALS")),
    "EditorDecreaseFontSize" to listOf(KeyboardShortcut.fromString("meta pressed MINUS"),
                                       KeyboardShortcut.fromString("meta pressed UNDERSCORE")),
    "EditorResetFontSize" to listOf(KeyboardShortcut.fromString("meta pressed 0")),
    "MinimizeCurrentWindow" to listOf(KeyboardShortcut.fromString("meta pressed M")),
    "Find" to listOf(KeyboardShortcut.fromString("meta pressed F")),
    "Replace" to listOf(KeyboardShortcut.fromString("meta alt pressed F")),
    "FindNext" to listOf(KeyboardShortcut.fromString("meta pressed G"), KeyboardShortcut.fromString("pressed F3")),
    "FindPrevious" to listOf(KeyboardShortcut.fromString("shift meta pressed G"), KeyboardShortcut.fromString("shift pressed F3")),
    "ReplaceInPath" to listOf(KeyboardShortcut.fromString("shift meta alt pressed F")),
    "GotoFile" to listOf(KeyboardShortcut.fromString("meta pressed P")),
    "SearchEverywhere" to listOf(KeyboardShortcut.fromString("meta pressed PERIOD")),
    "GotoLine" to listOf(KeyboardShortcut.fromString("meta pressed L")),
    "CodeCompletion" to listOf(KeyboardShortcut.fromString("ctrl pressed SPACE")),
    "EditorDeleteToLineEnd" to listOf(KeyboardShortcut.fromString("ctrl pressed K")),
    "EditorPreviousWord" to listOf(KeyboardShortcut.fromString("alt pressed LEFT")),
    "EditorNextWord" to listOf(KeyboardShortcut.fromString("alt pressed RIGHT")),
    "EditorDeleteToWordStart" to listOf(KeyboardShortcut.fromString("alt pressed BACK_SPACE")),
    "EditorDeleteToWordEnd" to listOf(KeyboardShortcut.fromString("alt pressed DELETE")),
    "EditorDuplicate" to listOf(KeyboardShortcut.fromString("shift meta pressed D")),
    "StepOver" to listOf(KeyboardShortcut.fromString("shift meta pressed O"), KeyboardShortcut.fromString("pressed F10")),
    "StepInto" to listOf(KeyboardShortcut.fromString("shift meta pressed I"), KeyboardShortcut.fromString("meta pressed F11")),
    "StepOut" to listOf(KeyboardShortcut.fromString("shift meta pressed U"), KeyboardShortcut.fromString("shift meta pressed F11")),
    "ViewBreakpoints" to listOf(KeyboardShortcut.fromString("meta alt pressed B")),
    "ToggleLineBreakpoint" to listOf(KeyboardShortcut.fromString("meta pressed BACK_SLASH"), KeyboardShortcut.fromString("pressed F9")),
    "RiderRemoveAllLineBreakpoints" to listOf(KeyboardShortcut.fromString("shift meta pressed F9")),
    "RunToCursor" to listOf(KeyboardShortcut.fromString("meta pressed F10")),
    "GotoDeclaration" to listOf(KeyboardShortcut.fromString("meta pressed D"), KeyboardShortcut.fromString("pressed F12")),
    "GotoImplementation" to listOf(KeyboardShortcut.fromString("meta pressed I")),
    "FindUsages" to listOf(KeyboardShortcut.fromString("shift meta pressed R"), KeyboardShortcut.fromString("shift pressed F12")),
    "ShowIntentionActions" to listOf(KeyboardShortcut.fromString("alt pressed ENTER"))
  )
}
```

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