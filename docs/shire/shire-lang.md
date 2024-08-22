---
layout: default
title: Shire Language Introduction
parent: Shire Language
nav_order: 1
---

Shire 主要由两部分组成：

- Hobbit Hole，用于定义数据处理流程与 IDE 交互 逻辑。
- Shire Template，用于编译和生成最终的提示词。

## Hobbit Hole 

Here is the detail

- Condition Visible: `when` condition to display the code block
- Variables:
  - Context Variables: `context` to get the context of the current file 
  - Pattern Action: use Pattern (Regex) to match the source data, and use Unix-like command to process the data.
- PostMiddle code processor
- Output Control Flow

## Shire Template

We use Velocity Template to generate the final prompt, and you can access the context variables in the template.

```shire
Explain follow code

$beforeCursor
```
