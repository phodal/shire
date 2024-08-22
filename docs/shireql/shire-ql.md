---
layout: default
title: ShireQL
nav_order: 5
has_children: true
permalink: /shireql
---

ShireQL 是一个基于 IDE 的数据查询语言，它允许你查询当前文件的 AST（抽象语法树）、Git、依赖信息等。它在 Shire
中用于定义当前文件的上下文以及可以在当前文件上执行的操作。

## 其它相关资源

### GitQL

GQL: [https://github.com/AmrDeveloper/GQL](https://github.com/AmrDeveloper/GQL)

```sql
SELECT author_name, COUNT(author_name) AS commit_num
FROM commits
GROUP BY author_name, author_email
ORDER BY commit_num DESC LIMIT 10
SELECT commit_count
FROM branches
WHERE commit_count BETWEEN 0..10

SELECT *
FROM refs
WHERE type = "branch"
SELECT *
FROM refs
ORDER BY type

SELECT *
FROM commits
SELECT author_name, author_email
FROM commits
SELECT author_name, author_email
FROM commits
ORDER BY author_name DESC, author_email ASC
SELECT author_name, author_email
FROM commits
WHERE name LIKE "%gmail%"
ORDER BY author_name
SELECT *
FROM commits
WHERE LOWER(name) = "amrdeveloper"
SELECT author_name
FROM commits
GROUP By author_name
SELECT author_name
FROM commits
GROUP By author_name
having author_name = "AmrDeveloper"

SELECT *
FROM branches
SELECT *
FROM branches
WHERE is_head = true
SELECT name, LEN(name)
FROM branches

SELECT *
FROM tags
SELECT *
FROM tags OFFSET 1 LIMIT 1
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

