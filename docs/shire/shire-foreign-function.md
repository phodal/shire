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

## Quick Start

The Shire code:

```shire
---
functions:
  normal: ".shire/ffi/hello.js"(string)
variables:
  "text": /.*ple.shire/ { normal("world") }
---

hello, $text
```

hello.js:

```javascript
const args = process.argv.slice(2);
console.log(args[0]);
```
