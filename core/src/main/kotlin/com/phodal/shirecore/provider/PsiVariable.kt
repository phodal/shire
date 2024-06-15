package com.phodal.shirecore.provider

/**
 * Enum representing variables used in the generation of code structures.
 */
enum class PsiVariable(val variableName: String) {
    /**
     * Represents the PsiNameIdentifierOwner of the current class, used to retrieve the class name.
     */
    CURRENT_CLASS_NAME("currentClassName"),

    /**
     * Represents the input and output of PsiElement and PsiFile.
     */
    CURRENT_CLASS_CODE("currentClassCode"),

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
    IS_NEW_FILE("isNewFile"),

    /**
     * The name of the target test file where the code structure will be generated.
     */
    TARGET_TEST_FILE_NAME("targetTestFileName"),

    /**
     * underTestMethod
     */
    UNDER_TEST_METHOD_CODE("underTestMethodCode")
}