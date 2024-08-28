---
layout: default
title: On Paste modify
parent: Shire Examples
nav_order: 9
---

`OnPaste` 可以在用户粘贴代码时，根据上下文，对用户粘贴的代码进行优化。

```shire
---
name: "PasteMaster"
interaction: OnPaste
---

优化待复制代码。根据当前的代码上下文（光标前后），对用户复制的代码，生成新的代码。

光标前的代码：

$beforeCursor

光标后的代码：

$afterCursor

用户复制的代码：

$text

只根据上下文，优化用户复制的代码
```

