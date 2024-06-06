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
name: "Summary"
description: "Generate Summary"
interaction: AppendCursor
actionLocation: ContextMenu
---

Summary webpage:

/browse:https://www.phodal.com
```

