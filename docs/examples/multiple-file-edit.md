---
layout: default
title: Multiple File Edit
parent: Shire Examples
nav_order: 12
---

ChatBox 是在 Shire ToolWindow 中的一个输入框，用户可以在这里输入内容，然后调用大语言模型。使用事项如下：

- 默认使用 `RigthPanel` 作为展示位置，使用 `ChatBox` 作为 `actionLocation`。
- 当用户创建了 `actionLocation: ChatBox` 的 Shire 代码时，将会读取用户的输入作为提示词的一部分。

```shire
---
name: "shire multiple file edit"
description: "Shire Multiple File Edit"
onStreaming: { logging }
interaction: RightPanel
actionLocation: ChatBox
---

根据用户的要求和现有的代码编写 Java 代码。要求：

1. 使用 diff patch 的方式。
2. 如果是新文件也使用 patch 的格式。
3. 每个文件的修改也请用 diff 的形式给出。

用户的需求如下：

$chatPrompt
```