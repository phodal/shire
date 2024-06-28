---
layout: default
title: afterStreaming
parent: Lifecycle
nav_order: 4
---

`afterStreaming` 会在执行完 `onStreamingEnd` 后执行，用于下一步的处理，因此也叫 `TaskRoutes`。 `TaskRoutes`
顾名思义，是一系列的任务处理路由，将根据条件执行不同的任务。

### 多条件

```shire
---
afterStreaming: {
    condition {
      "error"       { output.length < 1 }
      "json-result" { jsonpath("${'$'}.store.*") }
    }
    case condition {
      "error"       { notify("Failed to Generate JSON") }
      "json-result" { execute("sample.shire") } /* go to execute sample.shire */
      default       { notify("Failed to Generate JSON") /* mean nothing */ }
    }
  }
---
```

当 LLM 返回的结果是 JSON 时:

```json
{
  "store": {
    "book": [
      {
        "category": "reference",
        "author": "Nigel Rees",
        "title": "Sayings of the Century",
        "price": 8.95
      }
    ]
  }
}
```

可以匹配到 `json-result` 条件，然后执行 `sample.shire`。
