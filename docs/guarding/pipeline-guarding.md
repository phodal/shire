---
layout: default
title: Pipeline 函数保护数据
nav_order: 1
parent: AI 数据安全保护
---

使用 Pipeline 函数保护数据

### Sed

Basic Sed Example

```shire
---
name: Summary
description: "Generate Summary"
interaction: AppendCursor
variables:
  "var2": /.*ple.shire/ { cat | grep("fileName") | sed("\"fileName\"", "hello.kt") }
---

Summary webpage: ${'$'}var2
```

OpenAI Example:

```shire
---
name: Summary
description: "Generate Summary"
interaction: AppendCursor
variables:
  "openai": "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"
  "var2": /.*ple.shire/ { cat | grep("openai") | sed("(?i)\b(sk-[a-zA-Z0-9]{20}T3BlbkFJ[a-zA-Z0-9]{20})(?:['|\"|\n|\r|\s|\x60|;]|${'$'})", "sk-***") }
---

Summary webpage: $var2
```

### Pattern Sample

- OpenAI
  - Pattern `(?i)\b(sk-[a-zA-Z0-9]{20}T3BlbkFJ[a-zA-Z0-9]{20})(?:['|\"|\n|\r|\s|\x60|;]|$)`

  