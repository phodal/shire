---
layout: default
title: Http API Tool
nav_order: 2
parent: Cloud
---

在 [#11](https://github.com/phodal/shire/issues/11) 中，我们引入了一个远程调用的能力，即你可以在 Shire 中调用远程 API，作为上下文
的一部分。

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
