---
layout: default
title: FAQ
nav_order: 999
has_children: true
permalink: /faq
---

## `java.net.SocketException: No route to localhost:0 port is out of range`

**原因分析**：

此错误通常是因为IDE（例如IntelliJ IDEA）设置了手动代理，而错误信息中的“localhost:0”是配置项中的主机名和端口号参数所导致的。
参考[Issue93](https://github.com/phodal/shire/issues/93)来解决该问题。

**解决步骤**：

- 检查IDE的代理设置是否正确。
- 确认代理服务器的主机名和端口是否可用。
- 如果不需要代理，尝试关闭IDE的代理设置。
- 根据具体情况调整IDE配置或代理服务器配置。

## `RunCode: No run service found for file`

可能原因：

- 缺少对应语言的 IDE 插件：诸如 [HttpClient](https://plugins.jetbrains.com/plugin/13121-http-client)、JavaScript 插件等。
- 缺少对应语言的 FileRunService 实现。
