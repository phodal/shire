<p align="center">
  <img src="plugin/src/main/resources/META-INF/pluginIcon.svg" width="160px" height="160px"  alt="logo" />
</p>
<h1 align="center">Shire - AI Agents Language</h1>
<p align="center">
  <a href="https://github.com/phodal/shire/actions/workflows/build.yml">
    <img src="https://github.com/phodal/shire/workflows/Build/badge.svg" alt="Build" />
  </a>
  <a href="https://plugins.jetbrains.com/plugin/24549">
    <img src="https://img.shields.io/jetbrains/plugin/v/24549.svg" alt="Version" />
  </a>
  <a href="https://plugins.jetbrains.com/plugin/24549">
    <img src="https://img.shields.io/jetbrains/plugin/d/24549.svg" alt="Downloads" />
  </a>
</p>

Shire offers a straightforward AI Agents Language that enables communication between an LLM and control IDE for automated programming.

For example:

```shire
---
name: "Object Demo"
description: "A simple object demo"
interaction: AppendCursor
actionLocation: ContextMenu
postProcessor: ["VerifyCode", "RunCode"]
filenameFilter:
  "**/*.java": "You MUST use should_xx_xx style for test method name"
---

Generate code for API.

@api-market intergration User login oauth API

Here is current $language code:

/file:src/main/kotlin/com/phodal/blog/controller/UserController.kt
```

![Shire Cheatsheet](docs/images/shire-sheet.svg)

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "shire"</kbd> >
  <kbd>Install</kbd>
  
- Manually:

  Download the [latest release](https://github.com/phodal/shire/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Roadmap

- [ ] Shire syntax: Front-matter
  - [ ] Custom Interaction
  - [ ] Custom Agent
  - [ ] Custom RAG
- [ ] LLM Integration
- [ ] Architecture analysis
- [ ] ComfyUI like LLM Workflow
   - [ ] DAG - Directed Acyclic Graph 
   - [COMFYUI LLM PARTY](https://github.com/heshengtao/comfyui_LLM_party) 

## LICENSE

MPL License
