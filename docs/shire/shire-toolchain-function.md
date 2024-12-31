---
layout: default
title: Shire Toolchain Function
parent: Shire Language
nav_order: 7
---

# Toolchain Function

> Toolchain 函数默认遵循 Pattern-Action 模式，用于定义数据处理逻辑。在接受参数时，默认的第一个参数为上下文变量，即
`lastResult`

## Git

支持的函数：

- commit，提交代码，参数 1：`message`
- push，推送代码

### 示例：

```shire
---
name: "Auto Commit and Push"
afterStreaming: {
  case condition {
    default { print("feat: add auto commit and push sample") | commit | push }
  }
}
---

hi
```

## Database

支持的函数：

- `table`，获取数据库表信息，参数 1：`databaseName`，默认获取第一个连接的数据库。
- `column`，获取数据库列信息，参数 1：`tableName`，默认获取第一个表的列信息。
- `query`，执行 SQL 查询，参数 1：`sql`，示例：`query("select * from user")`

### 示例 1

```shire
---
variables:
  "relatedTableInfo": /./ { column("user", "post", "tag") }
---

根据如下的信息，生成 SQL：

$relatedTableInfo
```

## WireMock

支持的函数：

- `mock`，启动 WireMock 服务，参数 1：`filePath`。默认 8080 端口。

### 示例

```shire
---
name: "sample"
variables:
  "mock": /any/ { mock("samples/mock/blog_v0-stubs.json") }
---
```

其中的 `samples/mock/blog_v0-stubs.json` 文件内容如下：

```json
{
  "mappings": [
    {
      "request": {
        "method": "POST",
        "url": "/blog",
        "bodyPatterns": [
          {
            "matchesJsonPath": "$.title"
          },
          {
            "matchesJsonPath": "$.content"
          },
          {
            "matchesJsonPath": "$.author"
          }
        ]
      },
      "response": {
        "status": 201,
        "headers": {
          "Content-Type": "application/json"
        },
        "body": "{\"message\": \"Blog post created successfully\", \"id\": 1}"
      }
    }
  ]
}
```
