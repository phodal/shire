---
layout: default
title: Example Query Expression
parent: Shire Language
nav_order: 7
---

Usecases:

- ExampleDocument/Comment

## Example Query Expression

Example AST query expression which will support use our AST node and S-Expression to query the AST tree.

For example:

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

