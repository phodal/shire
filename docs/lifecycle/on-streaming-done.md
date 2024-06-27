---
layout: default
title: onStreamingDone
parent: Lifecycle
nav_order: 3
---

`onStreamingDone` 即在 Streaming 完成后通过一系列的后处理器对生成的内容进行处理，诸如：

- ParseCode，从生成的结果中解析生成的代码块。
- SaveFile，将保存生成的代码到文件。
- CodeVerify，检查代码错误或 PSI 问题
- RunCode，运行生成的代码（取决于是否存在对应的 RunService）。

### 示例：

```shire
---
onStreamingEnd: { parseCode | saveFile | verifyCode | runCode }
---

生成一个 python hello world，使用 markdown block  返回
```

### 内置 PostHandler

最新版本见源码：com.phodal.shirecore.middleware.BuiltinPostHandler

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
    VerifyCode("verifyCode"),

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
