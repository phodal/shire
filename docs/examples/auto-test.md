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
    when: { $fileName.contains(".java") && $filePath.contains("src/main/java") }
    variables:
      "frameworkContext": /.*/build\.gradle\.kts/ { grep("org.springframework.boot:spring-boot-starter-jdbc") | print("This project use Spring Framework")}
    ---
    Write unit test for following ${context.language} code.
    
    ${frameworkContext}
    
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
    ${context.selection}
    ```
    
    #if($context.isNewFile)
    Should include package and imports. Start method test code with Markdown code block here:
    #else
    Should include package and imports. Start ${context.targetTestFileName} test code with Markdown code block here:
    #end

