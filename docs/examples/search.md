---
layout: default
title: Semantic Search
parent: Shire Examples
nav_order: 8
---

## Semantic Search

- splitting, splitting the file
  - splitting by the file extension
  - support format: `*.docx`, `*.md`, `*.txt`, `*.pdf`, `*.xlsx`, `*.pptx` or other text-based file
- embedding, embedding full path of the file
  - embedding to InMemory: [InMemoryEmbeddingSearchIndex]
- searching, searching for a specific string in the file, which will
  - embedding the input string
  - execute relevant search
  - return result
- caching, caching the search result (Should be in first of calling)
  - support for local, remote, or InMemory cache?

### Basic Example


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

### Cache Example

```shire
---
name: "Search"
variables:
  "testTemplate": /.*.java/ { caching("disk") | splitting | embedding | searching("comment") }
---

根据如下的代码，回答用户的问题：博客创建的流程

$testTemplate
```
