---
layout: default
title: Shire PSI Query Expression
parent: Shire Language
nav_order: 8
---

Shire PSI Query Language, is a query language that allows you to query the AST of the current file. It is used in Shire
to define the
context of the current file and to define the actions that can be performed on the current file.

## Possible Design

Design

```shire
---
variables
  "var1": query {
     // 变量声明部分
     from  {} // datasource, like, dir, file symbol
     // 条件部分 
     where {} //  AST expand, and functions support for  regex, and methods: similar search, embedding search, tf-idf, and other advanced search
     // 结果部分
     select {} // Node, or Node's attribute, or Node's children 
  }
---
```

For example:

```shire
---
variables:
  "allController": {
    from {
        PsiClass clazz // here is a comment
    }
    where {
        clazz.extends("org.springframework.web.bind.annotation.RestController") and clazz.getAnAnnotation() == "org.springframework.web.bind.annotation.RequestMapping"
    }
    select {
        clazz.id, clazz.name, "code"
    }
  }
---
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

```codeql
import java

from Class c, Class superclass
where superclass = c.getASupertype()
select c, "This class extends the class $@.", superclass, superclass.getName()
```

Java

```codeql
from Person p
where parentOf(p) = parentOf("King Basil") and
  not p = "King Basil"
  and not p.isDeceased()
select p
```

JavaScript

```codeql
import javascript

from Comment c
where c.getText().regexpMatch("(?si).*\\bTODO\\b.*")
select c
```

Better Java Example:

```codeql
import java

from Constructor c, Annotation ann, AnnotationType anntp
where ann = c.getAnAnnotation() and
    anntp = ann.getType() and
    anntp.hasQualifiedName("java.lang", "SuppressWarnings")
select ann, ann.getValue("value")
```

Java 2

```codeql
import java

from LTExpr expr
where expr.getLeftOperand().getType().hasName("int") and
    expr.getRightOperand().getType().hasName("long") and
    exists(LoopStmt l | l.getCondition().getAChildExpr*() = expr) and
    not expr.getAnOperand().isCompileTimeConstant()
select expr
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

## Example Query Expression

Example AST query expression which will support use our AST node and S-Expression to query the AST tree.

A ChatGPT generate for example:

```kotlin
val query = """
    (MethodDeclaration
        (Modifier public)
        (Type void)
        (Identifier main)
        (ParameterList
            (Parameter
                (Type String)
                (Identifier args)
            )
        )
        (Block
            (Statement
                (ExpressionStatement
                    (MethodCall
                        (Identifier println)
                        (Arguments
                            (Expression
                                (Literal "Hello, World!")
                            )
                        )
                    )
                )
            )
        )
    )
""".trimIndent()
```
