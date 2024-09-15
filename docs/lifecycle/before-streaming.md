---
layout: default
title: beforeStreaming
parent: Lifecycle
nav_order: 4
---

`beforeStreaming` 即在 Streaming 开始前对生成的内容进行处理。

### 示例：启动 MockServer

```shire
---
name: "Blog.sample"
beforeStreaming: { mock("docs/mock_v0-stubs.json") }
---

```

### 示例：执行 Gradle Task

```shire
---
name: "Blog.sample"
beforeStreaming: { execute(":bootRun") }
onStreamingEnd: { parseCode | saveFile | openFile | runCode }
---

hi
```
