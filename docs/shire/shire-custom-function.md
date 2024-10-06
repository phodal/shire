---
layout: default
title: Shire Custom Function
parent: Shire Language
nav_order: 8
---

# Custom Function

{: .note }
注意：在 `0.9` 版本后支持

```shire
---
functions:
  aFunc: "defaultOutput.py"(string)
  aFunc: "multipleOutput.py"("data.txt") -> content, size
  bFunc: "path/access.py"($1, $2) -> analysisResult
  cFunc: "accessFunctionIfSupport.py"::resize("photo.jpg", 800, 600) -> image
---
```
