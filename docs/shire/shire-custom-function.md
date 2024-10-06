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
  aFunc: { "fileHandler.py"::read("data.txt") -> content }
  bFunc: { "analytics/dataAnalyzer.py"::analyzeData($1, $2) -> analysisResult }
  cFunc: { "imageEditor.py"::resize("photo.jpg", 800, 600) -> image }
---
```
