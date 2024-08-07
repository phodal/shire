---
layout: default
title: Shire Query Language
parent: Shire Language
nav_order: 5
---

Shire 查询语言是一种查询语言，允许你查询当前文件的 AST（抽象语法树）、Git、依赖信息等。它在 Shire 中用于定义当前文件的上下文以及可以在当前文件上执行的操作。

## ShireQL 基本语法 

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

## ShireQL 查询版本管理 

### Git 示例

详细见 [#41](https://github.com/phodal/shire/issues/41)

```shire
---
variables:
  "phodalCommits": {
    from {
        GitCommit commit
    }
    where {
        commit.authorName == "Phodal Huang"
    }
    select {
        commit.authorName, commit.authorEmail, commit.message
    }
  }
---
```

Model:

```
data class ShireGitCommit(
    val hash: String,
    val authorName: String,
    val authorEmail: String,
    val authorDate: Long,
    val committerName: String,
    val committerEmail: String,
    val committerDate: Long,
    val message: String,
    val fullMessage: String
) : GitEntity()
```

Model design for #41

- GitCommit
  - Usage: support for git commit query
  - Field: author, authorEmail, committer, committerEmail, hash, date, message, fullMessage
- FileCommit
  - Usage: support for file in history
  - Field: commit, filename, status, path
- Branch
  - Usage: support for branch query
  - Field: name, commitCount

Ref design: https://github.com/AmrDeveloper/GQL

## ShireQL 查询制品信息

### Maven 示例

```shire
---
variables:
  "mavenDependencies": {
    from {
        Dependency dependency
    }
    where {
        dependency.groupId == "org.springframework.boot" and dependency.artifactId == "spring-boot-starter-web"
    }
    select {
        dependency.groupId, dependency.artifactId, dependency.version
    }
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

## 其它相关资源

### GitQL

GQL: [https://github.com/AmrDeveloper/GQL](https://github.com/AmrDeveloper/GQL)

```sql
SELECT author_name, COUNT(author_name) AS commit_num FROM commits GROUP BY author_name, author_email ORDER BY commit_num DESC LIMIT 10
SELECT commit_count FROM branches WHERE commit_count BETWEEN 0 .. 10

SELECT * FROM refs WHERE type = "branch"
SELECT * FROM refs ORDER BY type

SELECT * FROM commits
SELECT author_name, author_email FROM commits
SELECT author_name, author_email FROM commits ORDER BY author_name DESC, author_email ASC
SELECT author_name, author_email FROM commits WHERE name LIKE "%gmail%" ORDER BY author_name
SELECT * FROM commits WHERE LOWER(name) = "amrdeveloper"
SELECT author_name FROM commits GROUP By author_name
SELECT author_name FROM commits GROUP By author_name having author_name = "AmrDeveloper"

SELECT * FROM branches
SELECT * FROM branches WHERE is_head = true
SELECT name, LEN(name) FROM branches

SELECT * FROM tags
SELECT * FROM tags OFFSET 1 LIMIT 1
```

### GitHub CodeQL

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


### SourceGraph CodeSearch

https://sourcegraph.com/docs/code-search/queries

```query
repo:^github\.com/sourcegraph/sourcegraph$ type:diff select:commit.diff.removed TODO

type:diff after:"1 week ago" \.subscribe\( lang:typescript

repo:github\.com/sourcegraph/sourcegraph$ (test AND http AND NewRequest) lang:go

```

Date function

```bash
before:"last thursday"
before:"november 1 2019"

after:"6 weeks ago"
after:"november 1 2019"

repo:vscode@*refs/heads/:^refs/heads/master type:diff task 
```

