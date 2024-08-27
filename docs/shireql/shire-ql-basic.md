---
layout: default
title: ShireQL Basic
parent: ShireQL
nav_order: 1
---

## ShireQL 基本语法

Design

```shire
---
variables
  "var1": query {
     // 变量声明部分
     from  {} // datasource，如：`PsiClass`, `GitCommit`, `ProjectDependency` 
     // 条件部分 
     where {} //  AST expand, and functions support for  regex, and methods: similar search, embedding search, tf-idf, and other advanced search
     // 结果部分
     select {} // Node, or Node's attribute, or Node's children 
  }
---
```

## 通用函数

### 常用函数

- date 函数

### NLP 函数 (待定)

- similarSearch
- embeddingSearch
- tf-idf