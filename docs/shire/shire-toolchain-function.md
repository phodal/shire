---
layout: default
title: Shire Toolchain Function
parent: Shire Language
nav_order: 7
---

# Toolchain Function

> Toolchain 函数默认遵循 Pattern-Action 模式，用于定义数据处理逻辑。在接受参数时，默认的第一个参数为上下文变量，即 `lastResult`

## Database

```shire
---
variables:
  "customTables": /./ { database | table }
---

根据如下的信息，生成 SQL：

$customTables
```