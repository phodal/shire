---
layout: default
title: afterStreaming
parent: Lifecycle
nav_order: 4
---

afterStreaming aka `TaskRoutes` in source code.

```shire
---
afterStreaming: {
    condition {
      "variable-success" { $selection.length > 1 }
      "jsonpath-success" { jsonpath("/bookstore/book[price>35]") }
      default { true }
    }
    case condition {
      "variable-sucesss" { done }
      "jsonpath-success" { task() }
      default { task() }
    }
  }
---
```
