---
layout: default
title: Design Principle
nav_order: 2
---

# Design Principle

## IDE 即代码解释器

IDE 不仅仅是一个文本编辑器或编程环境，而是一个能够理解、处理和操作代码的工具。这种设计思想下，IDE 的角色不仅是为了编辑代码，
还包括了对代码进行语法分析、语义理解和操作执行的能力。

在 Shire 中，我们引入了一系列的设计原则，以支持 IDE 的智能化和自动化：

- **上下文感知与变量化**。定义了一系列上下文变量，例如选择的文本、光标前后的文本、文件名和路径等。这些变量可以帮助 IDE
  理解和操作代码的不同部分，
  使得人和AI都能根据上下文信息进行代码处理和分析。
- **Pattern-Action 模式的代码文件过滤**。借鉴Unix的Shell编程思想，引入了Pattern-Action概念。这种模式允许根据特定的模式（例如文件类型或内容特征）
  执行相应的操作（例如grep、sort、xargs）。通过这种模式，IDE可以根据用户定义的条件对文件和数据进行过滤和处理，提高了代码操作的灵活性和效率。
- **PSI 查询语言 AstQL**。用于描述和执行 PSI（Program Structure Interface）查询。DSL 定义了变量声明、条件查询和结果选择的结构。
  这种语言允许开发者和AI根据特定的语法结构和查询条件，从代码中提取和分析所需的信息。这样的设计使得IDE不仅仅是一个静态的文本编辑器，
  而是一个能够通过语义理解和查询执行来操作代码的智能工具。

示例：

```shire
---
variables:
  "var2": /.*.java/ { cat | grep("error.log") | sort | cat }
   "extContext": /build\.gradle\.kts/ { cat | grep("org.springframework.boot:spring-boot-starter-jdbc") | print("This project use Spring Framework")}
---

解释如下代码

相关上下文： $extContext

$selection
```    

在这个示例中，我们展示了如何利用 Shire 的设计原则和功能来解析代码和上下文信息。通过定义变量和执行特定的 Pattern-Action
操作，IDE 可以根据条件过滤和处理代码文件，同时通过 PSI 查询语言进行高级的代码结构分析和提取。这样的能力不仅提升了开发者的效率，还使得
AI 能够更智能地参与到代码开发和分析过程中。

## DSL 即通信语言协议

## AFU：原子性作为抽象（Atomic Functional Units）

原子功能单元（Atomic Functional Units, AFUs）是一种设计方法，旨在将复杂系统分解为独立且功能明确的最小操作单元。这种设计原则受到
Linux 设计思想的启发，强调模块化、独立性和简洁性。

1. 原子性和模块化。每个原子功能单元（AFU）都是独立且不可再分割的基本操作单元，采用模块化设计，能够独立执行特定任务，并可以自由组合和重用。
2. 简单接口与管道式处理。AFUs暴露简单明确的输入和输出接口，通过管道连接，形成高效且连贯的数据处理流程，隐藏内部实现细节，简化用户操作。
3. 高内聚低耦合 。每个AFU内部专注于特定任务（高内聚），与其他单元通过简单接口进行通信（低耦合），提高系统的灵活性、可维护性和扩展性。

以下是一个示例，展示如何对Java文件进行Embedding，以提供模板中的变量，作为LLM（大型语言模型）的上下文：

```shire
---
variables:
  "searchResult": /*.docx/ { splitting | embedding | searching("API 设计范式") }
---

根据如下的内容，总结一下 API 如何设计：

$searchResult
```

在这个示例中：

- splitting：将文档拆分为独立的部分，每个部分可以单独处理。
- embedding：对每个部分进行 Embedding，以便在 LLM 中使用。
- searching：在 Embedding 的内容中搜索特定的关键词或概念。

通过这种方式，我们可以将复杂的任务分解为独立的原子功能单元，每个单元都是独立的，可以自由组合和重用，从而提高系统的灵活性和可维护性。
