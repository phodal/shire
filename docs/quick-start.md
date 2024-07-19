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

## Usage

1. first create new file, like `sample.shire`, with content:

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

当前我们只支持 OpenAI API 风格的 SAAS 服务，你需要在 `Settings` -> `Tools` -> `Shire` 中配置你的 API Token 和 Model。

### GLM 示例

- LLM API Host: https://open.bigmodel.cn/api/paas/v4/chat/completions
- ModelName: glm-4
- Engine Token: xxxx

