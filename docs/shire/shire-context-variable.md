---
layout: default
title: Shire Context Variable
parent: Shire Language
nav_order: 5
---

## Basic Context Variables

```kotlin
enum class ContextVariable(val variable: String, val description: String) {
    SELECTION("selection", "The selected text"),
    BEFORE_CURSOR("beforeCursor", "The text before the cursor"),
    AFTER_CURSOR("afterCursor", "The text after the cursor"),
    FILE_NAME("fileName", "The name of the file"),
    FILE_PATH("filePath", "The path of the file"),
    METHOD_NAME("methodName", "The name of the method"),
    LANGUAGE("language", "The language of the file"),
    COMMENT_SYMBOL("commentSymbol", "The comment symbol of the language"),
    FRAMEWORK_CONTEXT("frameworkContext", "The context of the framework"),
    ALL("all", "All the text")
    ;
}
```

## PSI Context Variables

```kotlin
enum class PsiContextVariable(val variableName: String) {
    /**
     * Represents the PsiNameIdentifierOwner of the current class, used to retrieve the class name.
     */
    CURRENT_CLASS_NAME("currentClassName"),

    /**
     * Represents the input and output of PsiElement and PsiFile.
     */
    CURRENT_CLASS_CODE("currentClassCode"),

    CURRENT_METHOD_NAME("currentMethodName"),

    CURRENT_METHOD_CODE("currentMethodCode"),

    /**
     * Represents the input and output of PsiElement and PsiFile.
     */
    RELATED_CLASSES("relatedClasses"),

    /**
     * Uses TfIDF to search for similar test cases in the code.
     */
    SIMILAR_TEST_CASE("similarTestCase"),

    /**
     * Represents the import statements required for the code structure.
     */
    IMPORTS("imports"),

    /**
     * Flag indicating whether the code structure is being generated in a new file.
     */
    IS_NEED_CREATE_FILE("isNeedCreateFile"),

    /**
     * The name of the target test file where the code structure will be generated.
     */
    TARGET_TEST_FILE_NAME("targetTestFileName"),

    /**
     * underTestMethod
     */
    UNDER_TEST_METHOD_CODE("underTestMethodCode"),

    /**
     * Represents the framework information required for the code structure.
     */
    FRAMEWORK_CONTEXT("frameworkContext"),

    /**
     * codeSmell
     */
    CODE_SMELL("codeSmell")
    ;
}
```