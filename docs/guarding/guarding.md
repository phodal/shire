---
layout: default
title: AI 数据安全保护
nav_order: 6
has_children: true
permalink: /guarding
---

Shire 在 AI 数据安全保护方面提供了一系列的功能，用于对 AI 模型的输入、输出数据进行保护。这些功能包括：

- `beforeStreaming`，在 Streaming 开始前对生成的内容进行处理，将敏感信息替换为占位符。
- `onStreaming`，在 Streaming 过程中对生成的内容，检查是否有敏感信息。
