---
layout: default
title: Shire Toolchain Function
parent: Shire Language
nav_order: 7
---

# Toolchain Function

> Toolchain 函数默认遵循 Pattern-Action 模式，用于定义数据处理逻辑。在接受参数时，默认的第一个参数为上下文变量，即 `lastResult`

## Database

支持的函数：

- `table`，获取数据库表信息，参数 1：`databaseName`，默认获取第一个连接的数据库。
- `column`，获取数据库列信息，参数 1：`tableName`，默认获取第一个表的列信息。

### 示例 1

```shire
---
variables:
  "relatedTableInfo": /./ { column("user", "post", "tag") }
---

根据如下的信息，生成 SQL：

$relatedTableInfo
```
