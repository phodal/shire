---
layout: default
title: Shire Environment
parent: Shire Language
nav_order: 9
---

Shire Environment 用于定义 Shire 的环境变量，用于存储一些敏感信息。 使用方式 `.shireEnv.json` 文件来存储环境变量，Shire
将会自动加载这种文件。

当前 Shire Env 支持两种变量：

- `development`：配置 Token、API Key 等信息。
- `models`：配置模型信息（在 `0.7.4` 版本后支持）。

## `.shireEnv.json` 文件

`.shireEnv.json` 用于存储环境变量，Shire 将会自动加载这种文件，当前只支持 `development` 环境。

```json
{
  "development": {
    "apiKey": "xxx"
  },
  "models": [
    {
      "title": "quickModel",
      "apiKey": "sk-xxx",
      "model": "gpt-4o-mini",
      "temperature": 0.3
    },
    {
      "title": "gpt4o",
      "apiKey": "sk-xxx",
      "model": "gpt-4o"
    },
    {
      "title": "glm-4-plus",
      "apiKey": "xxx",
      "model": "glm-4-plus",
      "apiBase": "https://open.bigmodel.cn/api/paas/v4/chat/completions"
    }
  ]
}
```

### 使用你的  apiKey

在 Shell 中放在 `${}` 中即可：

```shell
curl -X POST 'https://api.dify.ai/v1/completion-messages' \
  --header "Authorization: Bearer ${apiKey}" \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "inputs": {"feature": "Hello, world!", "story_list": ${storyList}},
      "response_mode": "streaming",
      "user": "phodal"
  }'
```

### Model 配置详细示例：

```kotlin
class LlmConfig(
    val title: String,
    val provider: String = "openai",
    val apiBase: String = "https://api.openai.com/v1/chat/completions",
    val apiKey: String,
    val model: String,
    val temperature: Double = 0.0,
    val maxTokens: Int? = 1024
)
```
