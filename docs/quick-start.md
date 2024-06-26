---
layout: default
title: Quick Start
nav_order: 2
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

## LICENSE

This code is distributed under the MPL 2.0 license. See `LICENSE` in this directory.
