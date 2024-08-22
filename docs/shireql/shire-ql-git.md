---
layout: default
title: ShireQL Git Query
parent: ShireQL
nav_order: 3
---


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
