# theShire Language and Compiler


## Http Request

    ```http request
    ### GET request to example server
    GET https://examples.http-client.intellij.net/get$END$
    ?generated-in=IntelliJ IDEA
    ```

## OpenWrite

Docs: [https://docs.openrewrite.org/](https://docs.openrewrite.org/)

[Open Rewrite](https://github.com/openrewrite/rewrite) Fast, repeatable refactoring for developers

```yml
---
type: specs.openrewrite.org/v1beta/recipe
name: com.yourorg.FindAndReplaceJDK17
displayName: Find and replace JDK 17 example
recipeList:
  - org.openrewrite.text.FindAndReplace:
      find: eclipse-temurin:17-jdk-jammy
      replace: eclipse-temurin:21.0.2_13-jdk-jammy
      filePattern: 'Dockerfile'
```
