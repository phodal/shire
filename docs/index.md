---
layout: default
title: Home
description: Shire offers a straightforward AI Coding Agent Language that enables communication between an LLM and control IDE for automated programming.
nav_order: 1
permalink: /
---

<p align="center">
  <img src="images/pluginIcon.svg" width="160px" height="160px"  alt="logo" />
</p>
<h1 align="center">Shire - AI Coding Agent Language</h1>
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

## English Introduction

**Shire** offers a seamless AI coding agent language, enabling large language models (LLMs) to engage in fluid dialogue
with integrated development environments (IDEs) to achieve automated programming.

> The concept of Shire has its roots in [AutoDev](https://github.com/unit-mesh/auto-dev), a subproject of 
> [UnitMesh](https://unitmesh.cc/). Within AutoDev, we envisioned an AI-driven integrated development environment 
> for developers, which included Shire’s predecessor, DevIns. DevIns was designed to empower users to create custom AI 
> agents tailored to their own IDEs, thus forging a personalized AI-powered development realm.

## Chinese Introduction

PS：the Shire 一词来自于《魔戒》(LOTR）中的（夏尔）Shire，是霍比特人（Hobbit）的家园。

Shire 提供了一种简便的 AI 编码智能体语言，使大型语言模型（LLM）能够与集成开发环境（IDE）无缝交互，实现编程自动化。

> Shire 的概念起源于 [AutoDev](https://github.com/unit-mesh/auto-dev)，这是 [UnitMesh](https://unitmesh.cc/) 的一个子项目。在
AutoDev 中，我们设计了一个面向开发者的 AI 驱动集成开发环境（IDE），其中包括 Shire 的前身 DevIns。DevIns 旨在让用户能够为他们自己的
IDE 创建定制的 AI 代理，从而构建个性化的 AI 驱动开发环境。


Shire example Project: [Java example](https://github.com/shire-lang/shire-spring-java-demo)

### SDLC

- [Code change analysis](https://github.com/shire-lang/shire-demo/blob/master/.shire/requirement/crud/analysis-requirements.shire) use LLM to analysis requirements, then choose the best files to change.
- [Requirement + AutoCRUD](https://github.com/shire-lang/shire-demo/blob/master/.shire/requirement/analysis-requirements.shire) analysis requirements, then auto generate CRUD code.
- [Dify + OpenAPI/Swagger](https://github.com/shire-lang/shire-demo/blob/master/.shire/api/design/design-rest-api.shire) interactive with Dify agent to design REST API
- [Add Spring doc to project](https://github.com/shire-lang/shire-demo/blob/master/.shire/api/setup-dep/setup-spring-doc-openapi.shire) add Spring doc to project.
- [Generate RestAssured Test](https://github.com/shire-lang/shire-demo/blob/master/.shire/api/verify/rest-assure.shire) AI to generate RestAssured test code.
- [Generate JavaDoc](https://github.com/shire-lang/shire-demo/blob/master/.shire/documentation/javadoc.shire) use LLM to generate JavaDoc.
- [Complexity Analysis](https://github.com/shire-lang/shire-demo/blob/master/.shire/git/complexity.shire) calculate code complexity.
- [PlantUML: fetch Github issue for analysis](https://github.com/shire-lang/shire-demo/blob/master/.shire/requirement/visual/mindmap.shire) fetch GitHub issue to generate mindmap.

FrontEnd:

- [Frontend + HTML mockup](https://github.com/shire-lang/shire-demo/blob/master/.shire/frontend/html-mock-up.shire) use LLM to generate HTML mockup and show in WebView.
- [Mobile + Ionic](https://github.com/shire-lang/shire-demo/blob/master/.shire/frontend/mobile-mock-up.shire) use LLM to generate mobile mockup with Ionic, show in WebView.
- [Mobile + React](https://github.com/shire-lang/shire-demo/blob/master/.shire/frontend/react-mock-up.shire) use LLM to generate mobile mockup with React, show in WebView.
- [JavaScript Auto Unittest](https://github.com/shire-lang/shire-demo/blob/master/.shire/frontend/js-test.shire) use LLM to generate JavaScript test code.

Test:

- [E2E Test: Playwright](https://github.com/shire-lang/shire-demo/blob/master/.shire/api/e2e/playwright.shire) AI to use Playwright to test the API and auto execute test.
- [API Test: Java](https://github.com/shire-lang/shire-demo/blob/master/.shire/test/java/api-test.shire) use LLM to generate Java API test code.
- [Unit Test: Java](https://github.com/shire-lang/shire-demo/blob/master/.shire/test/java/autotest.shire) use LLM to generate Java unit test code.
- [Unit Test: Python](https://github.com/shire-lang/shire-demo/blob/master/.shire/test/python/AutoTest.shire) use LLM to generate Python unit test code.
- [Unit Test: Golang](https://github.com/shire-lang/shire-demo/blob/master/.shire/test/go/AutoTest.shire) use LLM to generate Golang unit test code.

### Workflow & IDE Integration

- [Capture web pages and generate report](https://github.com/shire-lang/shire-demo/blob/master/.shire/research/research.shire) capture web pages and generate report.
- [approvalExecute](https://github.com/shire-lang/shire-demo/blob/master/.shire/approve/approve.shire) waiting for approval to execute next shire code
- [Custom InlineChat](https://github.com/shire-lang/shire-demo/blob/master/.shire/chatbox/inline-chat.shire)  custom inline chat
- [Custom ChatBox](https://github.com/shire-lang/shire-demo/blob/master/.shire/chatbox/wrapper-chat.shire) custom prompt to use right panel chat box
- [Python as Foreign Function Interface](https://github.com/shire-lang/shire-demo/blob/master/.shire/ffi/python-shell-thread.shire) use Python to run shell command in thread.
- [Quick Input](https://github.com/shire-lang/shire-demo/blob/master/.shire/miscs/quick-input.shire) show quick input dialog.
- [Terminal Agent](https://github.com/shire-lang/shire-demo/blob/master/.shire/miscs/terminal.shire) use terminal agent to run shell command.

### EcoSystem

- [Git: Auto push code](https://github.com/shire-lang/shire-demo/blob/master/.shire/git/auto-push.shire) auto commit and push code to server.
- [Git: diff AI changed code](https://github.com/shire-lang/shire-demo/blob/master/.shire/git/diff-example.shire) diff AI changed code.
- [Git: Commit message](https://github.com/shire-lang/shire-demo/blob/master/.shire/git/login-commit-message.shire) generate commit message.
- [Git: Commit ID with Jira](https://github.com/shire-lang/shire-demo/blob/master/.shire/git/commit-message.shire) generate commit message with Jira ID.
- [Database: GitHub issue + Design Database Schema](https://github.com/shire-lang/shire-demo/blob/master/.shire/database/design-db.shire) fetch GitHub issue as context to design database schema
- [Database: Run SQL in Database](https://github.com/shire-lang/shire-demo/blob/master/.shire/database/command.shire) run SQL with `/database` command.
- [OpenRewrite: generate refactoring code](https://github.com/shire-lang/shire-demo/blob/master/.shire/refactor/openRewrite.shire) use OpenRewrite to generate refactoring code.
- [MockServer: WireMock](https://github.com/shire-lang/shire-demo/blob/master/.shire/api/mock/gen-mock.shire) AI to generate mock server with WireMock and auto start mock server.
- [PlantUML: with remote Agent](https://github.com/shire-lang/shire-demo/blob/master/.shire/toolchain/puml/plantuml-remote.shire) use remote agent to generate PlantUML code.
- [Mermaid: with remote Agent](https://github.com/shire-lang/shire-demo/blob/master/.shire/toolchain/mermaid.shire) use remote agent to generate Mermaid code.
- [Sonarlint: fix issue](https://github.com/shire-lang/shire-demo/blob/master/.shire/toolchain/sonarfix.shire) use Sonarlint to fix issue.

## Shire Resources

Shire Cheatsheet

![Shire Cheatsheet](docs/images/shire-sheet.svg)

Shire Data Architecture:

![Shire Data Architecture](docs/images/shire-data-flow.svg)

Shire Resources

- Documentation: [Shire AI Coding Agent Language](https://shire.phodal.com/)
- [Shire Book: AI for software-engineering](https://aise.phodal.com/) (Chinese only)
- [Shire.Run - the shareable AI coding agent](https://shire.run/)

## Demo Video

Youtube:

[![Shire AI Coding Agent Language](https://img.youtube.com/vi/z1ijWOL1rFY/0.jpg)](https://www.youtube.com/watch?v=z1ijWOL1rFY)

Bilibili

[![Shire AI Coding Agent Language](https://img.youtube.com/vi/z1ijWOL1rFY/0.jpg)](https://www.bilibili.com/video/BV1Lf421q7S7/)
