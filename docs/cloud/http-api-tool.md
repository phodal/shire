---
layout: default
title: Http API Tool
nav_order: 2
parent: Cloud
---

在 [#11](https://github.com/phodal/shire/issues/11) 中，我们引入了一个远程调用的能力，即你可以在 Shire 中调用远程 API，作为上下文
的一部分。

## Quick Start

先看个例子：

```shire
---
variables:
  "demo": /demo.md/ { thread(".shire/toolchain/bigmodel.curl.sh") }
---

hi

$demo
```

在这个例子中，我们定义了一个变量 `demo`，我们调用 `bigmodel.curl.sh` 来获取一个远程的 API 数据。

如下是 `bigmodel.curl.sh` 的内容：

```shell
curl --location 'https://open.bigmodel.cn/api/paas/v4/chat/completions' \
--header 'Authorization: Bearer ${apiKey}' \
--header 'Content-Type: application/json' \
--data '{
    "model": "glm-4",
    "messages": [
        {
            "role": "user",
            "content": "你好"
        }
    ]
}'
```

这里我们使用了一个变量 `apiKey`，它可以通过 `*.shireEnv.json` 文件来设置

```json
{
  "development": {
    "apiKey": "123456"
  }
}
```

当前，只支持简单的环境变量，即上面的 `development` 为环境名，`apiKey` 为变量名。

### `.shireEnv.json` 文件

`.shireEnv.json` 用于存储环境变量，Shire 将会自动加载这种文件，当前只支持 `development` 环境。

### cURL.sh

在 Shire 中，我们使用 cURL 来调用远程 API，以简化调用的过程。 注意：

- Shire 通过 JetBrains 的 HttpClient 来转换 cURL 脚本，因此，不一定支持所有的 cURL 语法。
- Shire 只支持 `${xxx}` 形式的变量替换，不支持 `$xxx` 形式的变量替换。
- Shire 使用 OkHttpClient 来调用远程 API，因此，不一定支持所有的 cURL 语法。

### 结合 JsonPath

> JSONPath 是一种类似于 XPath 的语法，用于从 JSON 文档中选择数据。在 Shire 中，我们可以使用 JsonPath 来选择我们需要的数据。

```shire
---
variables:
  "api": /sampl.sh/ { thread(".shire/toolchain/bigmodel.curl.sh") | jsonpath("$.choices[0].message.content") }
---

hi

$api
```

输出示例：

```bash
Prepare for running httpClient.shire...
Shire Script: /Users/phodal/IdeaProjects/shire-demo/.shire/toolchain/httpClient.shire
Shire Script Compile output:
hi
你好👋！我是人工智能助手智谱清言，可以叫我小智🤖，很高兴见到你，欢迎问我任何问题。

--------------------
你好！很高兴见到你。如果你有任何问题或需要帮助，请随时告诉我。我在这里为你提供信息和支持。

Process finished with exit code 0
```

