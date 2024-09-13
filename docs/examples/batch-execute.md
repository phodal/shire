---
layout: default
title: Batch Execute
parent: Shire Examples
nav_order: 10
---

`batch` 函数可以用来批量执行一个脚本。这个函数接受两个参数，第一个参数是要执行的脚本，第二个参数是要执行的文件列表。

```shire
---
name: "Generate Swagger Doc"
variables:
  "controllers": /.*.Controller.java/ { print }
  "gen-swagger": /any/ { batch("controller-with-swagger.shire", $controllers) }
beforeStreaming: { stop }
---

```

