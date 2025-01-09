---
layout: default
title: Custom Inline Chat
parent: Shire Examples
nav_order: 11
---

Inline Chat 可以在当用户选中内容时，在选中行的左侧显示一个 Icon，用户点击这个 Icon 后，可以在当前页面内进行对话。

![Inline Chat](https://shire.run/images/shire-inline-chat.png)

```shire
---
name: "shire multiple file edit"
description: "Shire Multiple File Edit"
interaction: StreamDiff
actionLocation: InlineChat
---

根据用户的要求和现有的代码编写 Java 代码。要求：

1. 使用 diff patch 的方式。
2. 如果是新文件也使用 patch 的格式。
3. 每个文件的修改也请用 diff 的形式给出。

现有代码如下：

$all

用户的需求如下：

$chatPrompt
```
