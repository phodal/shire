---
layout: default
title: Quick Start
nav_order: 3
---


## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "shire"</kbd> >
  <kbd>Install</kbd>

- Get from Marketplace:

  <iframe width="245px" height="48px" src="https://plugins.jetbrains.com/embeddable/install/24549"></iframe>

- Manually:

  Download the [latest release](https://github.com/phodal/shire/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

Examples: [https://github.com/shire-lang/shire-spring-java-demo](https://github.com/shire-lang/shire-spring-java-demo)

## Usage

### "Hello World" example

1. create a new file, like `hi.shire`, with content:

```shire
hi
```

2. Run file with `Shire` plugin, and you will see AI response result.

```
Prepare for running hi.shire...
Shire Script: /Volumes/source/ai/shire/hi.shire
Shire Script Compile output:
Used model: gpt-4o
hi

--------------------
Hello! How's it going?

Process finished with exit code 0
```

### Example 2

1. first create a new file, like `sample.shire`, with content:

```shire
Explain code /file:src/main/java/com/example/Controller.java
```

2. Run file with `Shire` plugin, and you will see AI response result.

### Use in IDE

Example:

```shire
---
name: "AutoTest"
description: "AutoTest"
actionLocation: ContextMenu
interaction: AppendCursor
when: { $fileName.contains(".java") && $filePath.contains("src/main/java") }
---

@ext-context.autotest

Write unit test for following ${context.language} code.

${context.frameworkContext}

/file:src/main/kotlin/com/phodal/blog/controller/UserController.kt
```

## Config LLM

Current we support OpenAI, GLM, 零一万物, Moonshot AI, DeepSeek AI. You need to configure your API Token and Model in
`Settings` -> `Tools` -> `Shire`.

### OpenAI

- LLM API Host: Empty
- ModelName: gpt-4o
- Engine Token: your key

### GLM 示例

- LLM API Host: https://open.bigmodel.cn/api/paas/v4/chat/completions
- ModelName: glm-4
- Engine Token: your key

### 零一万物 

- LLM API Host: https://api.lingyiwangwu.com/v1/chat/completions
- ModelName: yi-34b-chat
- Engine Token: your key

### Moonshot AI

- LLM API Host: https://api.moonshot.cn/v1/chat/completions
- ModelName: moonshot-v1-8k
- Engine Token: your key

### DeepSeek AI

- LLM API Host: https://api.deepseek.com/v1/chat/completions
- ModelName: deepseek-chat
- Engine Token: your key


