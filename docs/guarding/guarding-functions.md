---
layout: default
title: AI 数据安全保护函数
nav_order: 2
parent: AI 数据安全保护
---

数据安全保护函数（AI Data Guarding Functions）是用于进行对与模型交互的数据进行数据保护、去敏感化等操作的一种机制。

## ShireGuarding.yaml (TBD)

todos:

- refs: https://github.com/mazen160/secrets-patterns-db/blob/master/datasets/high-confidence.yml

```yaml
keywords: data protection, data security, data masking, data anonymization, data encryption, data obfuscation, data redaction, data tokenization, data privacy, data protection, data security, data masking, data anonymization, data encryption, data obfuscation, data redaction, data tokenization, data privacy
patterns:
  - pattern:
      name: Slack Token
      regex: "(xox[pborsa]-[0-9]{12}-[0-9]{12}-[0-9]{12}-[a-z0-9]{32})"
      confidence: high
models:
  - name: MaliciousURLs
    location: ~/models/maliciousURLs
  - name: BanCode 
    location: ~/models/semanticCode
  - name: Gibberish # 检测乱码
    location: ~/models/gibberish
```
