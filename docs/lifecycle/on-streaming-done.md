---
layout: default
title: onStreamingDone
parent: Lifecycle
nav_order: 5
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
| UpdateEditorText  | 更新编辑器文本。        |
| Patch             | 打补丁。            |

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
| parseCode  | 从生成的结果中解析生成的代码块。                |
| saveFile   | 将保存生成的代码到文件。                    |
| openFile   | 打开生成的文件。                        |
| verifyCode | 检查代码错误或 PSI 问题。                 |
| runCode    | 运行生成的代码（取决于是否存在对应的 RunService）。 |

### 结合变量的示例

```shire
---
name: Summary
description: "Generate Summary"
interaction: AppendCursor
data: ["a", "b"]
when: $fileName.matches("/.*.java/")
variables:
  "var2": /.*ple.shire/ { cat | find("fileName") | sort }
onStreamingEnd: { append($var2) | saveFile("summary.md") }
---

Summary webpage: $fileName
```

这里的 `var2` 是一个正则表达式，用于匹配文件名中包含 `ple.shire` 的文件，然后将其追加到文件中。

`onStreamingEnd` 会在 Streaming 完成后执行，这里会将 `var2` 的内容追加到 output 中，最终保存到 `summary.md` 文件中。

### 提交信息生成示例

```shire
---
name: "Commit message"
interaction: AppendCursor
actionLocation: CommitMenu
onStreamingEnd: { parseCode | updateEditorText }
---

请为给定的变更（Diff）编写一个连贯但具有描述性的代码提交信息。

背景信息：我现在使用 Git 编写一本开源电子书《AI 辅助软件工程：AI IDE 插件与编程智能体示例》，我需要为每个提交编写一个简洁但具有描述性的提交信息。

要求：

- 确保包含修改了什么以及为什么。
- 以不超过 50 个字符的祈使句形式开头。
- 然后留下一个空行，如有必要，继续详细说明。
- 如果变更是一个 .shire 文件，说明我添加了一个新的示例。

遵循常规提交规范，例如：

- fix(authentication): 修复密码正则表达式模式问题
- feat(storage): 添加对S3存储的支持
- test(java): 修复用户控制器的测试用例
- docs(architecture): 在主页添加架构图

Diff：

$currentChanges
```
