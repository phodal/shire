---
layout: default
title: Development
nav_order: 8
has_children: true
permalink: /development
---

{: .note }
Due to my Sabbatical Leave and responsibilities in caring for my child, updates to this document may not be as frequent.

核心概念：

- ShireCompiler
- HobbitHole，the frontmatter config will convert to a HobbitHole, which is a data structure for the IDE action.

## IDE Build issue

### Notes: Kotlin 2.0 -> 1.9.24

```bash
Internal API usages (179): 
    #Internal class kotlinx.serialization.UnknownFieldException reference
        Internal class kotlinx.serialization.UnknownFieldException is referenced in com.phodal.shirecore.guard.model.SecretPatternItem$.serializer.deserialize(Decoder) : SecretPatternItem. This class is marked with Kotlin `internal` visibility modifier, indicating that it is not supposed to be referenced in client code outside the declaring module.
        Internal class kotlinx.serialization.UnknownFieldException is referenced in com.phodal.shirecore.llm.ChatMessage$.serializer.deserialize(Decoder) : ChatMessage. This class is marked with Kotlin `internal` visibility modifier, indicating that it is not supposed to be referenced in client code outside the declaring module.
    #Internal class kotlinx.serialization.internal.StringSerializer reference
        Internal class kotlinx.serialization.internal.StringSerializer is referenced in com.phodal.shirecore.agent.CustomFlowTransition$.serializer.childSerializers() : KSerializer[]. This class is marked with Kotlin `internal` visibility modifier, indicating that it is not supposed to be referenced in client code outside the declaring module.
        Internal class kotlinx.serialization.internal.StringSerializer is referenced in com.phodal.shirecore.agent.CustomAgent$.serializer.childSerializers() : KSerializer[]. This class is marked with Kotlin `internal` visibility modifier, indicating that it is not supposed to be referenced in client code outside the declaring module.

```

- Kaml 0.60.0 -> build with Kotlin 2.0.0, but it's not compatible with Kotlin 1.9.24.
