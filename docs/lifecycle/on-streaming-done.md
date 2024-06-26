---
layout: default
title: onStreamingDone
parent: Lifecycle
nav_order: 3
---

`onStreamingDone` 即在 Streaming 完成后通过一系列的后处理器对生成的内容进行处理，诸如：

- CodeVerify，检查代码错误或 PSI 问题
- RunCode，运行生成的代码
- ParseCode，解析生成的代码块

如下是内置的后处理器：

```kotlin
enum class BuiltinPostHandler(var handleName: String) {
    /**
     * Logging the action.
     */
    Logging("logging"),

    /**
     * Metric time spent on the action.
     */
    TimeMetric("timeMetric"),

    /**
     * Acceptance metric.
     */
    AcceptanceMetric("acceptanceMetric"),

    /**
     * Check has code error or PSI issue.
     */
    CodeVerify("codeVerify"),

    /**
     * Run generate text code
     */
    RunCode("runCode"),

    /**
     * Parse text to code blocks
     */
    ParseCode("parseCode"),

    /**
     * For example, TestCode should be in the correct directory, like java test should be in test directory.
     */
    InferCodeLocation("InferCodeLocation"),
}
```
