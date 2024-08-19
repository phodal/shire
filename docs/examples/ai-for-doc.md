---
layout: default
title: AI for Doc
parent: Shire Examples
nav_order: 1
---

在这篇文章里，我们将分享其中的三个实践：

1. 生成自定义风格注释
2. 借助 pipeline 函数，自动生成文档文件
3. 结合 RAG 技术，自动化分析文档

以及我们的一些思考。

## 经典文档工程的解决思路

过去在编写文档的一些痛点，诸如于：

- 文档代码不同步。即文档的 API 变化可能落后于代码，导致 API 与文档出现不一致。
- 频繁的 API 变更。API 变更时，文档需要手动进行更新，不能自动化同步。
- 概念不统一。对于同一个概念，文档的不同地方描述不一致。
- 重复的文档块。文档需要重复引用某一部分的文档，不能像代码一样引用。
- 代码无法运行。按照文档的步骤下来编写的代码、复制的代码，是不能运行的。

几年前，为了提供技术框架文档的质量，我研究了市面上主流的文档生成工具、框架文档构建等，也总结了一些文档生成的最佳实践，诸如：

- 《[文档代码化](https://www.phodal.com/blog/isomorphism-document/)》
- 《[文档同构：如何实现文档与代码的双向绑定？](https://www.phodal.com/blog/isomorphism-document/)》
- 《[文档工程体验设计：重塑开发者体验](https://www.phodal.com/blog/documentation-enginnering-experience-design/)》
- 《[API 库的文档体系支持：主流编程语言的文档设计](https://www.phodal.com/blog/api-ducumentation-design-dsl-base/)》

但是，这些工具都无法满足我的需求，所以在过去我也编写了一系列的文档生成工具，诸如：Forming （ https://github.com/inherd/forming ）

```Rust
// doc-code: file("src/lib.rs").line()[2, 5]
// 读取 "src/lib.rs" 文件的第 2 到第 5 行
// doc-section: file("src/lib.rs").section("section1")
// 读取 "src/lib.rs" 文件中的 section1 相关的代码块
```

但是，这并不是一个完美的解决方案，因为你经常要因为代码的变化而去更新文档。

## AI 增强技术文档写作体验

> AI 增强的技术文档写作体验是一种创新的方法，将先进的人工智能技术与文档编写和管理深度融合。它通过自动化工具和智能分析，简化了文档创建、
> 更新和维护的流程，显著提高了文档的质量、准确性和一致性。

作为一个实验项目，我们开始使用 Shire 来生成和维护技术文档。以下是几个主要场景示例：

- 代码注释生成：通过分析代码内容，自动生成相应的文档注释，确保文档与代码同步更新，并减少手动维护的需求。
- 自动化内容生成：基于已有的代码注释，自动生成完整的文档内容，包括 API 说明、使用示例等，显著降低了手动编写和更新文档的工作量。
- 代码示例生成：自动读取项目中的测试用例，并将其作为文档中的示例代码展示，帮助读者更好地理解代码的实际应用场景。
- 动态内容检索：根据特定关键词，智能检索文档内容，帮助用户快速定位所需信息，并自动生成相关文档段落。

通过智能自动化的介入，文档编写变得更加高效和轻松，开发者能够专注于核心开发任务，同时确保文档始终与最新的代码和功能保持同步。

## Shire 智能体语言示例

在这里，我们主要会使用 Shire 语言的三个基本能力：

- 借助 IDE 与项目和 LLM 进行交互
- 基于 pattern-action 的变量定义和生成
- 基于 RAG 函数的内容检索

相关的示例，可以直接阅读 Shire 中的代码：https://github.com/phodal/shire

### 基础能力：生成自定义风格注释

为了更好的让 LLM 理解代码的函数，我们需要先使用 Shire 编写一个生成注释的指令。如下代码所示：

    ---
    name: "生成注释"
    interaction: InsertBeforeSelection
    actionLocation: ContextMenu
    when: $fileName.contains(".kt") && $filePath.contains("src/main/kotlin")
    onStreamingEnd: { insertNewline | formatCode }
    ---
    
    为如下的代码编写注释，使用 KDoc 风格：
    
    ```$language
    $selection
    ```
    
    只返回注释

在这里，我们定义了一个专用于生成 Kotlin 代码注释的指令，通过右键菜单触发。当用户在 Kotlin 文件中选择代码后，Shire
会自动为选中的代码生成相应的注释，
并插入到代码之前。

### 读取与生成：借助 pipeline 函数，自动生成文档文件

随后，我们就可以根据目标文档路径，诸如 `docs/shire/shire-builtin-variable.md` 编写对应的生成逻辑。诸如于：

```shire
---
name: "Context Variable"
description: "Here is a description of the action."
interaction:  RunPanel
variables:
  "contextVariable": /ContextVariable\.kt/ { cat }
  "psiContextVariable": /PsiContextVariable\.kt/ { cat }
onStreamingEnd: { parseCode | saveFile("docs/shire/shire-builtin-variable.md") }
---

根据如下的信息，编写对应的 ContextVariable 相关信息的 markdown 文档。

你所需要包含的 ContextVariable 信息如下：

$contextVariable

...
```

在这里，我们定义了一个变量 `contextVariable`，它的值是读取所有的 `ContextVariable.kt` 文件的结果。在运行的时候，Shire
会将这个变量的值
编译到 prompt 中，并发送给 LLM，以生成对应的文档。当 LLM 生成的文档返回后，我们会解析出其中的代码块，并保存到指定的文件中。

除此，当代码库中包含有测试用例时，我们就可以配置示例作为代码示例：

```shire
---
name: "Hobbit Hole"
description: "Here is a description of the action."
interaction:  RunPanel
variables:
  "currentCode": /HobbitHole\.kt/ { cat }
  "testCode": /ShireCompileTest\.kt/ { cat }
onStreamingEnd: { saveFile("docs/shire/shire-hobbit-hole.md")  }
---

根据如下的代码用例、文档，编写对应的 HobbitHole 相关信息的 markdown 文档。
...

```

当然了，也可以直接读取原来的文档，然后进行更新。

### 示例：结合 RAG 技术，自动化分析文档

对于更复杂的场景，则可以直接结合 RAG 与 Shire 的 workflow 来实现。如下所示：

```shire
---
name: "Semantic Search"
variables:
  "code": /.*.kt/ { splitting | embedding }
  "input": "博客创建流程"
  "lang": "java"
afterStreaming: {
    case condition {
      default { searching($output) | execute("SummaryQuestion.shire", $output, $input, $lang) }
    }
 }
---
You are a coding assistant who helps the user answer questions about code in their workspace by providing a list of
 relevant keywords they can search for to answer the question.
...
```

上述代码中，我们定义了一个变量 `code`，它的值是对所有的 `*.kt` 文件进行分割，并进行向量化。而这里的的 `input` 则是用户输入的问题，
用于搜索相关的文档内容。

在执行时，会将用户的问题发送给
LLM，由其生成关键词，然后在本地进行检索，最后，将结果发送给下一个流程，即 `SummaryQuestion.shire`。
在 `SummaryQuestion.shire` 中，会将检索结果进行总结，然后生成对应的文档。

## 总结

在这篇文档中，我们分享了使用 Shire 智能体语言来生成和维护技术文档的经验和思考。Shire 是我们在开发中不断探索和改进的一种智能语言工具，
它不仅简化了文档编写的流程，还有效解决了传统文档编写中的诸多痛点。
