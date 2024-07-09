---
layout: default
title: onStreamingDone
parent: Lifecycle
nav_order: 3
---

`onStreamingDone` 即在 Streaming 完成后通过一系列的后处理器对生成的内容进行处理。

## 内置 PostHandler

内置的后处理器包括：

| 后处理器              | 描述              |
|-------------------|-----------------|
| Logging           | 记录操作日志。         |
| TimeMetric        | 记录操作耗时。         |
| AcceptanceMetric  | 接受度指标。          |
| VerifyCode        | 检查代码错误或 PSI 问题。 |
| RunCode           | 运行生成的文本代码。      |
| ParseCode         | 将文本解析为代码块。      |
| InferCodeLocation | 推断代码位置。         |
| SaveFile          | 将文件保存到磁盘。       |
| OpenFile          | 在编辑器中打开文件。      |
| InsertCode        | 在当前光标位置插入代码。    |
| FormatCode        | 格式化代码。          |
| ParseComment      | 解析注释为注释块。       |
| InsertNewline     | 插入新行。           |
| Append            | 将文本追加到文件中。      |

最新版本见源码：com.phodal.shirecore.middleware.BuiltinPostHandler

## 示例

### Hello, world  示例

```shire
---
onStreamingEnd: { parseCode | saveFile | openFile | verifyCode | runCode }
---

生成一个 python hello world，使用 markdown block  返回
```

该代码会调用 LLM 生成一个 python hello world，然后将生成的代码块解析，保存到文件，打开文件，检查代码错误或 PSI 问题，最后运行生成的代码。

对应的后处理器有：

| 后处理器       | 描述                              |
|------------|---------------------------------|
| ParseCode  | 从生成的结果中解析生成的代码块。                |
| SaveFile   | 将保存生成的代码到文件。                    |
| OpenFile   | 打开生成的文件。                        |
| VerifyCode | 检查代码错误或 PSI 问题。                 |
| RunCode    | 运行生成的代码（取决于是否存在对应的 RunService）。 |

### 结合变量的示例

```shire
---
name: Summary
description: "Generate Summary"
interaction: AppendCursor
data: ["a", "b"]
when: $fileName.matches("/.*.java/")
variables:
  "var2": /.*ple.shire/ { cat | grep("fileName") | sort }
onStreamingEnd: { append($var2) | saveFile("summary.md") }
---

Summary webpage: $fileName
```

这里的 `var2` 是一个正则表达式，用于匹配文件名中包含 `ple.shire` 的文件，然后将其追加到文件中。

`onStreamingEnd` 会在 Streaming 完成后执行，这里会将 `var2` 的内容追加到 output 中，最终保存到 `summary.md` 文件中。
