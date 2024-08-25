---
layout: default
title: Secondary Research (TBC)
parent: Shire Scene
nav_order: 1
---

> Desk Research（又称为二级研究）是一种利用已有的、已发布的信息进行分析和整理的研究方法。与初级研究不同，初级研究主要关注新数据的收集与生成，
而二级研究则专注于对已有数据和研究成果的总结、整合和综合分析。典型的二级研究来源包括教科书、百科全书、新闻文章、评论文章和元分析等。

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

- `capture` function, param1: Language, param2: NodeType
- `crawl` function, param1: URLs
- `thread` function, param1: Script, param2: Parameters

### API Resource by Bash

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

## AutoFeature

- autoAnalysis
- autoImage
- autoDraft
- autoSlide
- block embedded
- Notion like Block for Reference
- Daily randomTip
- toImage
- Icon

## Workflow Design

topic
- capture 
- summary 
- insight 
- express
