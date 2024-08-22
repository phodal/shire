---
layout: default
title: ShireQL PSI Query
parent: ShireQL
nav_order: 2
---

示例:

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

## ShireQL 查询抽象语法树

### Java 语言示例

```kotlin
enum class JvmPsiPqlMethod(val methodName: String, val description: String) {
    GET_NAME("getName", "Get class name"),
    NAME("name", "Get class name"),
    EXTENDS("extends", "Get class extends"),
    IMPLEMENTS("implements", "Get class implements"),
    METHOD_CODE_BY_NAME("methodCodeByName", "Get method code by name"),
    FIELD_CODE_BY_NAME("fieldCodeByName", "Get field code by name"),

    SUBCLASSES_OF("subclassesOf", "Get subclasses of class"),
    ANNOTATED_OF("annotatedOf", "Get annotated classes"),
    SUPERCLASS_OF("superclassOf", "Get superclass of class"),
    IMPLEMENTS_OF("implementsOf", "Get implemented interfaces of class"),
}
```