---
layout: default
title: Data Privacy
nav_order: 6
has_children: true
permalink: /data-privacy
---

Shire 在 AI 数据安全上提供了一些保护机制，用于对 AI 模型的输入、输出数据进行保护。

**Pipeline 函数保护数据**通过自定义规则对数据进行处理，诸如：

- PatternAction
  - 使用 `replace` 函数，将敏感信息替换为占位符。
- PsiMask
  - 使用 `mask` 函数，对敏感信息进行脱敏处理。

**AI 数据安全保护函数**用于对 AI 模型的输入、输出数据进行保护。主要是用在 AI 模型的输入输出数据中，对敏感信息进行保护，保护的方式包括：

- `beforeStreaming`，在 Streaming 开始前对生成的内容进行处理，将敏感信息替换为占位符。
- `onStreaming`，在 Streaming 过程中对生成的内容，检查是否有敏感信息。


