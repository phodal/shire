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
  normal: "defaultOutput.py"(string)
  output: "multipleOutput.py"(string) -> content, size
  special: "accessFunctionIfSupport.py"::resize(string, number, number) -> image
---
```
