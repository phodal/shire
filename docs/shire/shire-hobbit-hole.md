---
layout: default
title: Hobbit Hole Config
parent: Shire Language
nav_order: 2
---

Hobbit Hole 用于定义数据处理流程与 IDE 交互逻辑。

```shire
---
name: "AutoTest"
description: "AutoTest"
interaction: AppendCursor
actionLocation: ContextMenu
when: $fileName.contains(".java") && $filePath.contains("src/main/java")
fileName-rules:
  /.*Controller.java/: "When testing controller, you MUST use MockMvc and test API only."
variables:
  "extContext": /build\.gradle\.kts/ { cat | grep("org.springframework.boot:spring-boot-starter-jdbc") | print("This project use Spring Framework")}
  "testTemplate": /\(.*\).java/ {
    case "$1" {
      "Controller" { cat(".shire/templates/ControllerTest.java") }
      "Service" { cat(".shire/templates/ServiceTest.java") }
      default  { cat(".shire/templates/DefaultTest.java") }
    }
  }
  "allController": {
    from {
        PsiClass clazz /* sample */
    }
    where {
        clazz.getMethods().length() > 0
    }
    select {
        clazz.getMethods()
    }
  }
---
```