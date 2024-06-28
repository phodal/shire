---
layout: default
title: onStreamingDone
parent: Lifecycle
nav_order: 3
---

`onStreamingDone` 即在 Streaming 完成后通过一系列的后处理器对生成的内容进行处理。

示例：

```shire
---
onStreamingEnd: { parseCode | saveFile | openFile | verifyCode | runCode }
---

生成一个 python hello world，使用 markdown block  返回
```

该代码会调用 LLM 生成一个 python hello world，然后将生成的代码块解析，保存到文件，打开文件，检查代码错误或 PSI 问题，最后运行生成的代码。

对应的后处理器有：

- ParseCode，从生成的结果中解析生成的代码块。
- SaveFile，将保存生成的代码到文件。
- OpenFile，打开生成的文件。
- VerifyCode，检查代码错误或 PSI 问题
- RunCode，运行生成的代码（取决于是否存在对应的 RunService）。


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


    /**
     * Open file in the editor
     */
    OpenFile("openFile")
    ;
}
```
