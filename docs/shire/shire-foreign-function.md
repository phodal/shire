---
layout: default
title: Shire Foreign Function
parent: Shire Language
nav_order: 8
---

# Foreign Function

{: .note }
注意：在 `0.9` 版本后支持

```shire
---
functions:
  aFunc: "defaultOutput.py"(string)
  aFunc: "multipleOutput.py"(type) -> content, size
  bFunc: "path/access.py"(string, string) -> analysisResult
  cFunc: "accessFunctionIfSupport.py"::resize(string, number, number) -> image
---
```
