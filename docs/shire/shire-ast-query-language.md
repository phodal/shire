---
layout: default
title: Shire AST Query Language
parent: Shire Language
nav_order: 9
---

Shire AQL, is a query language that allows you to query the AST of the current file. It is used in Shire to define the 
context of the current file and to define the actions that can be performed on the current file.

## Possible Design

ChatGPT sample:

```aql
query {
  // 变量声明部分
  from {
    /* ... variable declarations ... */
  }

  // 条件部分
  where {
    /* ... logical formula ... */
  }

  // 结果部分
  select {
    /* ... expressions ... */
  }
}
```

Better sample:

```
query {
  from {
    variable_declaration
  }
  where {
    name = "myVariable"
  }
  select {
    name: name,
    type: type
  }
}
```

## Reference

### CodeQL

QL Language Reference: https://codeql.github.com/docs/ql-language-reference/queries/

```codeql
from /* ... variable declarations ... */
where /* ... logical formula ... */
select /* ... expressions ... */
```

For Example:

```sql
from int x, int y
where x = 3 and y in [0 .. 2]
select x, y, x * y as product, "product: " + product
```

### TreeSitter

TreeSitter Query Language Reference: https://tree-sitter.github.io/tree-sitter/using-parsers#query-syntax

```tree-sitter
(query
  (function_definition
    name: (identifier) @function-name))
```

### XPath

XPath Query Language Reference: https://www.w3schools.com/xml/xpath_intro.asp

```xpath
//bookstore/book[price>35]
```

### JSON Path

JSON Path Query Language Reference: https://goessner.net/articles/JsonPath/

```jsonpath
$.store.book[*].author
```

