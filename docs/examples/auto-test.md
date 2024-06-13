---
layout: default
title: AI AutoTest
parent: Shire Examples
nav_order: 1
---

    ---
    name: "AutoTest"
    description: "AutoTest"
    interaction: AppendCursor
    actionLocation: ContextMenu
    when: $fileName.contains(".java") && $filePath.contains("src/main/java")
    fileName-rules:
      /.*Controller.java/: "When testing controller, you MUST use MockMvc and test API only."
    variables:
      "frameworkContext": /.*/build\.gradle\.kts/ { grep("org.springframework.boot:spring-boot-starter-jdbc") | print("This project use Spring Framework")}
    ---
    Write unit test for following ${context.language} code.
    
    ${context.frameworkContext}
    
    #if($context.relatedClasses.length() > 0 )
    Here is the relate code maybe you can use
    ${context.relatedClasses}
    #end
    
    #if($context.currentClass.length() > 0 )
    This is the class where the source code resides:
    ${context.currentClass}
    #end
    
    Here is the source code to be tested:
    
    ```${context.language}
    ${context.imports}
    ${context.sourceCode}
    ```
    
    #if($context.isNewFile)
    Should include package and imports. Start method test code with Markdown code block here:
    #else
    Should include package and imports. Start ${context.testClassName} test code with Markdown code block here:
    #end

