---
layout: default
title: FAQ
nav_order: 999
has_children: true
permalink: /faq
---

---
layout: default
title: FAQ
nav_order: 999
has_children: true
permalink: /faq
---

# 常见问题

## 如何快速获得帮助？

1. 使用搜索引擎：尝试使用搜索引擎如Bing、百度或Google来查找解决方案。
2. 向社区求助：如果你的问题在搜索引擎中没有找到答案，可以考虑提issues或向社交媒体上的网友求助。确保你的问题描述包含足够的背景信息和具体细节，这样更有可能得到有用的回复。

## 提示：`java.net.SocketException: No route to localhost:0 port is out of range`
**原因分析**：
此错误通常是因为IDE（例如IntelliJ IDEA）设置了手动代理，而错误信息中的“localhost:0”是配置项中的主机名和端口号参数所导致的。参考[Issue93](https://github.com/phodal/shire/issues/93)来解决该问题。

**解决步骤**：
- 检查IDE的代理设置是否正确。
- 确认代理服务器的主机名和端口是否可用。
- 如果不需要代理，尝试关闭IDE的代理设置。
- 根据具体情况调整IDE配置或代理服务器配置。