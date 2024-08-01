---
layout: default
title: Pipeline 函数保护数据
nav_order: 1
parent: Data Privacy
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

- OpenAI: `(?i)\b(sk-[a-zA-Z0-9]{20}T3BlbkFJ[a-zA-Z0-9]{20})(?:['|\"|\n|\r|\s|\x60|;]|$)`
- GitHub: `ghp_[0-9a-zA-Z]{36}`

Normal: https://github.com/mazen160/secrets-patterns-db/blob/master/datasets/git-leaks.yaml

```yaml
patterns:
  - pattern:
      name: Env Var
      regex: "(?i)(apikey|secret|key|api|password|pass|pw|host)=[0-9a-zA-Z-_.{}]{4,120}"
      confidence: high
  - pattern:
      name: Generic Credential
      regex: "(?i)(dbpasswd|dbuser|dbname|dbhost|api_key|apikey|secret|key|api|password|user|guid|hostname|pw|auth)(.{0,20})?['|\"]([0-9a-zA-Z-_\\/+!{}/=]{4,120})['|\"]"
      confidence: high
```

### PII Sample

[db/pii-stable.yml](https://github.com/mazen160/secrets-patterns-db/blob/master/db/pii-stable.yml)

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

### Resources

[Secrets Patterns Database](https://github.com/mazen160/secrets-patterns-db) The largest open-source database for
detecting secrets, API keys, passwords, tokens, and more. Use secrets-patterns-db to feed your secret scanning engine
with regex patterns for identifying secrets.

