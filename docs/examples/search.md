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
  "*.docx": /.*/build\.gradle\.kts/ { splitting | embedding | searching("hello") }
---
Write unit test for following ${context.language} code.
```

- splitting, splitting the file
- embedding, embedding full path of the file
- indexing, indexing the file
- relevant, searching for a specific string in the file
