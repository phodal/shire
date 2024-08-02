---
layout: default
title: AI 数据安全保护函数
nav_order: 2
parent: Data Privacy
---

数据安全保护函数（AI Data Guarding Functions）是用于进行对与模型交互的数据进行数据保护、去敏感化等操作的一种机制。

- NER 命名实体识别 (Named-entity recognition) Scanner
- Pattern/Regex Scanner

## `redact` function

在 redact 函数中, 我们使用 [db/pii-stable.yml](https://github.com/mazen160/secrets-patterns-db/blob/master/db/pii-stable.yml) 
作为敏感数据的配置文件, 用于对数据进行脱敏处理。

普通变量使用示例：

```shire
---
variables:
  "phoneNumber": "086-1234567890"
  "var2": /.*ple.shire/ { cat | redact }
---
```    

RAG 场景示例：

```shire

```

## 自定义 `.shireSecretPattern.yaml`（TBD）

- refs: https://github.com/mazen160/secrets-patterns-db/blob/master/datasets/high-confidence.yml

Todos:

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
