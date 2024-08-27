---
layout: default
title: Secondary Research
parent: Shire Scene
nav_order: 1
---

> Desk Research（又称为二级研究）是一种利用已有的、已发布的信息进行分析和整理的研究方法。与初级研究不同，初级研究主要关注新数据的收集与生成，
> 而二级研究则专注于对已有数据和研究成果的总结、整合和综合分析。典型的二级研究来源包括教科书、百科全书、新闻文章、评论文章和元分析等。

这些文献通常不会包含详细的“方法”部分，因为它们依赖的是已有的研究数据，而非原始数据的生成。

## WebResource

```shire
---
variables:
  "websites": /*\.md/ { capture("docs/crawlSample.md", "link") | crawl() | thread("summary.shire") }
  "confluence": { thread("confluence.bash", param1, param2) }
  "pythonNode.js": { thread("python.py", param1, param2) }  
---

[website]: it will extract all the .md files and crawl them, then thread them with summary.shire, then return the result.
```

- `capture` 函数, 参数 1: Language, 参数 2: AST Node Type
- `crawl` 函数, 参数 1: URLs: List<String>
- `thread` 函数, 参数 1: `Script File`, 参数...: Parameters：`Array<String>`
    - 如果 crawl 返回的是一个数组，那么 thread 会对数组中的每一个元素执行一次

## 真实世界示例：AI 辅助运维桌面研究

详细见如下的代码示例：

### Main.shire

```shire
---
variables:
  "crawl": /crawlSample\.md/ { capture("docs/crawlSample.md", "link") | crawl() | thread(".shire/research/summary.shire") }
  "article": /crawlSample\.md/ { cat }
onStreamingEnd: { saveFile("docs/output.md") }
---

根据如下的草稿和对应的资料，编写一篇对应主题的文章。

文章草稿如下：

$article

相关的资料如下：

$crawl
```

### summary.shire

```shire
使用中文总结如下的开源项目。

要求：

1. 给出项目的基本介绍、首页和文档地址
2. 列举 5 个关键特性

$output
````

### crawlSample.md

```md
# AI 辅助软件工程：CLI 命令生成

## 为什么需要 AI 来辅助 CLI？

## 什么是 CLI 命令生成

## 行业示例

1. [nvtop](https://github.com/Syllo/nvtop) - <small>NVIDIA GPUs htop like monitoring tool</small>
2. [nvitop](https://github.com/XuehaiPan/nvitop) - <small>An interactive NVIDIA-GPU process viewer and beyond.</small>
3. [aichat](https://github.com/sigoden/aichat) - <small>all-in-one AI powered CLI chat and copilot.</small>
4. [aider](https://github.com/paul-gauthier/aider) - <small>AI pair programming in your terminal</small>
5. [elia](https://github.com/darrenburns/elia) - <small>A TUI ChatGPT client built with Textual</small>
6. [gpterminator](https://github.com/AineeJames/ChatGPTerminator) - <small>A TUI for OpenAI's ChatGPT</small>
7. [gtt](https://github.com/eeeXun/gtt) - <small>A TUI for Google Translate, ChatGPT, DeepL and other AI
   services.</small>
8. [ollama](https://github.com/ollama/ollama) - <small>get up and running with large language models locally.</small>
9. [oterm](https://github.com/ggozad/oterm) - <small>A text-based terminal client for ollama.</small>
10. [tgpt](https://github.com/aandrew-me/tgpt) - <small>AI Chatbots in the terminal without needing API keys.</small>
11. [yai](https://github.com/ekkinox/yai) - <small>Your AI powered terminal assistant</small>
```

## API Resource by Bash

- Confluence API
- Jira API

#### Confluence API

https://developer.atlassian.com/cloud/confluence/using-the-rest-api/

```bash
curl --request <method> '/rest/api/content/search?limit=1&cql=id!=0 order by lastmodified desc' \
--header 'Accept: application/json' \
--header 'Authorization: Basic <encoded credentials>'
```

#### Jira API

https://developer.atlassian.com/cloud/jira/platform/rest/v3/intro/

```bash
curl --request <method> '<url>?<parameters>' \
--header 'Accept: application/json' \
Authorization: Basic <encoded credentials>'
```

## Design Pattern

### AutoFeature

- autoAnalysis
- autoImage
- autoDraft
- autoSlide
- block embedded
- Notion like Block for Reference
- Daily randomTip
- toImage
- Icon

### Workflow Design

topic

- capture
- summary
- insight
- express
