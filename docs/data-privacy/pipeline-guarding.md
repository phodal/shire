---
layout: default
title: Pipeline 函数
nav_order: 1
parent: Data Privacy
---

### 使用 Sed 函数保护数据

Basic Sed Example

```shire
---
variables:
  "var2": /.*ple.shire/ { cat | find("fileName") | sed("\"fileName\"", "hello.kt") }
---

Summary webpage: $var2
```

OpenAI Example:

```shire
---
variables:
  "openai": "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"
  "var2": /.*ple.shire/ { cat | find("openai") | sed("(?i)\b(sk-[a-zA-Z0-9]{20}T3BlbkFJ[a-zA-Z0-9]{20})(?:['|\"|\n|\r|\s|\x60|;]|${'$'})", "sk-***") }
---

Summary webpage: $var2
```

## 相关资源

### [Secrets Patterns DB](https://github.com/mazen160/secrets-patterns-db)

Secrets Patterns DB 包含了用于检测秘密、API 密钥、密码、令牌等的正则表达式模式的最大开源数据库。

示例：[db/pii-stable.yml](https://github.com/mazen160/secrets-patterns-db/blob/master/db/pii-stable.yml)

部分内容如下：

```yaml
patterns:
  - pattern:
      name: times
      regex: \d{1,2}:\d{2} ?(?:[ap]\.?m\.?)?|\d[ap]\.?m\.?
      confidence: high
  - pattern:
      name: phones
      regex: ((?:(?<![\d-])(?:\+?\d{1,3}[-.\s*]?)?(?:\(?\d{3}\)?[-.\s*]?)?\d{3}[-.\s*]?\d{4}(?![\d-]))|(?:(?<![\d-])(?:(?:\(\+?\d{2}\))|(?:\+?\d{2}))\s*\d{2}\s*\d{3}\s*\d{4}(?![\d-])))
      confidence: high
  - pattern:
      name: phones_with_exts
      regex: ((?:(?:\+?1\s*(?:[.-]\s*)?)?(?:\(\s*(?:[2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\s*\)|(?:[2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\s*(?:[.-]\s*)?)?(?:[2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\s*(?:[.-]\s*)?(?:[0-9]{4})(?:\s*(?:#|x\.?|ext\.?|extension)\s*(?:\d+)?))
      confidence: high
  - pattern:
      name: emails
      regex: ([a-z0-9!#$%&'*+\/=?^_`{|.}~-]+@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?)
      confidence: high
```

