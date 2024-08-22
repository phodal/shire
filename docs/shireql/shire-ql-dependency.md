---
layout: default
title: ShireQL Dependency Query
parent: ShireQL
nav_order: 4
---

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
