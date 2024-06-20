---
layout: default
title: Shire AST Query Language
parent: Shire Language
nav_order: 9
---

Shire AQL, is a query language that allows you to query the AST of the current file. It is used in Shire to define the 
context of the current file and to define the actions that can be performed on the current file.



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

