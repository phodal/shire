---
layout: default
title: LLM Guarding
nav_order: 6
has_children: true
permalink: /guarding
---

LLM Guarding 是用于进行数据保护、去敏感化等操作的一种机制。使用场景：

- `beforeStreaming`，在 Streaming 开始前对生成的内容进行处理，将敏感信息替换为占位符。
- `onStreaming`，在 Streaming 过程中对生成的内容，检查是否有敏感信息。

