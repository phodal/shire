---
layout: default
title: Shire Foreign Function
parent: Shire Language
nav_order: 8
---

# Foreign Function

{: .note }
注意：在 `0.9` 版本后支持

示例：

```shire
---
functions:
  normal: "defaultOutput.py"(string)
  output: "multipleOutput.py"(string) -> content, size // TODO, No Implemented
  special: "accessFunctionIfSupport.py"::resize(string, number, number) -> image // TODO, No Implemented
---
```

语言支持：

- Node.js (`.js` 文件)
- Shell (`.sh` 文件)
- Python (`.py` 文件)
- Kotlin Script (`.kts` 文件)

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

### JavaScript example

```javascript
const args = process.argv.slice(2);
console.log(args[0]);
```

### Kotlin Script example

In Kotlin Script mode, you can just use `args` without `main` function.

```kotlin
if (args.isNotEmpty()) {
    println("${args[0]}!")
} else {
    println("No args...")
}
```