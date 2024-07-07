---
layout: default
title: Code Refactoring
parent: Shire Examples
nav_order: 8
---

```shire
---
name: "AutoTest"
description: "AutoTest"
interaction: AppendCursor
variables:
  "*.docx": /.*/build\.gradle\.kts/ { embed | embedSearch("hello") }
---
Write unit test for following ${context.language} code.
```