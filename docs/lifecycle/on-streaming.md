---
layout: default
title: onStreaming
parent: Lifecycle
nav_order: 2
---

{: .note }
> **Note:** This page is reserved for future use. It will be used to describe the `onStreaming` lifecycle event.


示例：

```shire
---
onStreaming: { logging }
---
```

当前支持的函数：

- logging,
- timing,
- profiling,

### logging

> 记录 LLM prompt 日志。

- `.shire-output/logging.log` 是 LLM 日志文件
- `.shire-output/logging.jsonl` 是含历史 LLM 日志的 JSON 格式文件。

详细见：LoggingStreamingService

### timing

> 记录生成 LLM prompt 时间。

### profiling

> 记录 LLM prompt 的性能分析，主要包含内存。
