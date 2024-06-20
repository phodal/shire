package com.phodal.shirecore.provider.variable

/**
 * Enum representing variables used in the generation of code structures.
 */
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
    FRAMEWORK_CONTEXT("frameworkContext")
    ;

    companion object {
        /**
         * Returns the PsiVariable with the given variable name.
         *
         * @param variableName the variable name to search for
         * @return the PsiVariable with the given variable name
         */
        fun fromVariableName(variableName: String): PsiContextVariable? {
            return values().firstOrNull { it.variableName == variableName }
        }
    }
}