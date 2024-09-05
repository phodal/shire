---
layout: default
title: Remote AI Agent
parent: Workflow
nav_order: 2
---

Remote AI Agent 是通过调用远程的 AI Agent 来执行任务。主要实现方式：

- `thread` 来调用 `.curl.sh` 脚本，执行远程 AI Agent 的任务

示例：

```shire
---
variables:
  "story": /any/ { thread(".shire/shell/dify-epic-story.curl.sh") | jsonpath("$.answer", true) }
---
```

## 代码定位示例

入口 Shire 文件

```shire
---
name: "定位待变更代码"
variables:
  "story": /any/ { thread(".shire/shell/dify-user-story-workflow.curl.sh") | jsonpath("$.answer", true) }
  "controllers": /.*.java/ { cat | grep("class\s+([a-zA-Z]*Controller)")  }
  "services": /.*.java/ { cat | grep("class\s+([a-zA-Z]*Service)")  }
  "firstController": /CinemaController\.java/ { print }
  "firstService": /CinemaController\.java/ { print }
  "domainLanguage": /domain-language\.csv/ { cat }
onStreamingEnd: { parseCode | openFile }
---

你是一个网站资深的开发人员，能帮助我定位到代码文件。请根据如下的用户故事，以及对应的 controller, service 名称，选择最合适修改的代码文件

用户故事：

$story

Controller 列表：

$controllers

Service 列表：

$services

这个网站的一些专有名词如下：

$domainLanguage

要求：

如果没有合适的 controller，请给出最合适的 controller 和 service 路径。

Controller 示例路径在：

$firstController

service 示例路径在：

$firstService

你只返回文件名，格式如：`src/main/xxx/DemoController.java`

请严格按格式返回，只返回存在的代码文件，只返回文件路径。
```

`dify-user-story-workflow.curl.sh` 代码：

```bash
curl -X POST 'https://api.dify.ai/v1/completion-messages' \
  --header "Authorization: Bearer ${singleStoryKey}" \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "inputs": {"feature": "Hello, world!", "story_list": "作为购物中心电影观众，我想要提前预订电影场次相关的食物，以便于节省购买食物的时间，更好地安排观影时间。", "story": "添加零食和饮料至购物车影院观众在购票时添加零食和饮料提前准备好观影期间的零食和饮料"},
      "response_mode": "streaming",
      "user": "phodal"
  }'
```

其中的 `singleStoryKey` 可以通过在项目中创建 `xx.shireEnv.json` 来支持，示例：

```json
{
  "development": {
    "singleStoryKey": "xxxx"
  }
}
```