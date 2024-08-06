---
layout: default
title: AI 数据安全保护函数
nav_order: 2
parent: Data Privacy
---

数据安全保护函数（AI Data Guarding Functions）是用于进行对与模型交互的数据进行数据保护、去敏感化等操作的一种机制。

- NER 命名实体识别 (Named-entity recognition) Scanner
- Pattern/Regex Scanner

## `redact` 函数

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

## 使用自定义 `.shireSecretPattern.yaml`

在 Shire 中支持与 [Secrets Patterns DB](https://github.com/mazen160/secrets-patterns-db) 相似的配置文件，用于对数据进行脱敏处理。
你可以在项目中新建一个 `.shireSecretPattern.yaml`结尾的文件，用于定义自定义的敏感数据规则，如：`Phodal.shireSecretPattern.yaml`。

在该文件中，你可以定义一些敏感数据的规则，如：

```yaml
patterns:
  - pattern:
      name: Slack Token
      regex: "(xox[pborsa]-[0-9]{12}-[0-9]{12}-[0-9]{12}-[a-z0-9]{32})"
      confidence: high
```

随后，Shire 将会在处理数据时，自动对匹配到的数据进行脱敏处理。
