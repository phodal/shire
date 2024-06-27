---
layout: default
title: afterStreaming
parent: Lifecycle
nav_order: 4
---

afterStreaming aka `TaskRoutes` in source code.

### 多条件

```shire
---
onStreamingEnd: { parseCode("json") }
afterStreaming: {
    condition {
      "error"       { output.length < 1 }
      "json-result" { jsonpath("$.store.*") }
    }
    case condition {
      "error"       { notify("Failed to Generate JSON") }
      "json-result" { execute("sample.shire") }
      default       { notify("Failed to Generate JSON") /* mean nothing */ }
    }
  }
---
```

