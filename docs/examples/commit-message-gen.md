---
layout: default
title: AI Commit Message
parent: Shire Examples
nav_order: 2
---

```shire
---
name: "Commit message"
interaction: AppendCursor
actionLocation: CommitMenu
---

为给定的变更（Diff）编写一个连贯但具有描述性的代码提交信息。

- 确保包含修改了什么以及为什么。
- 以不超过 50 个字符的祈使句形式开头。
- 然后留下一个空行，如有必要，继续详细说明。
- 说明应该少于 200 个字符。

遵循常规提交规范，例如：

- fix(authentication): 修复密码正则表达式模式问题
- feat(storage): 添加对S3存储的支持
- test(java): 修复用户控制器的测试用例
- docs(architecture): 在主页添加架构图

Diff：

$currentChanges
```
