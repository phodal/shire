# theShire Language and Compiler


## Http Request

    ```http request
    ### GET request to example server
    GET https://examples.http-client.intellij.net/get$END$
    ?generated-in=IntelliJ IDEA
    ```

## Custom File Object with glob

```shire
---
filenameRules:
  "**/*.java": "You MUST use should_xx_xx style for test method name, You MUST use given-when-then style."
  "**/*Controller.java": "Use appropriate Spring test annotations such as `@MockBean`, `@Autowired`, `@WebMvcTest`, `@DataJpaTest`, `@AutoConfigureTestDatabase`, `@AutoConfigureMockMvc`, `@SpringBootTest` etc."
  "**/*Service.java": "Follow the common Spring code style by using the AssertJ library.\nAssume that the database is empty before each test and create valid entities with consideration for data constraints (jakarta.validation.constraints)."
libraryRule: 
  "junit5": "This project uses JUnit 5, you should import `org.junit.jupiter.api.Test` and use `@Test` annotation."
  "junit4": "This project uses JUnit 4, you should import `org.junit.Test` and use `@Test` annotation."
---

[should be in lowcase]: "You should use lowcase for file name."
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
