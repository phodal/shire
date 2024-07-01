package com.phodal.shirecore.provider.variable

/**
 * Represents the context variables that can be used in the code structure generation process.
 *
 * @property variableName the name of the context variable
 */
enum class PsiContextVariable(val variableName: String, val description: String?) {
    /**
     * Represents the PsiNameIdentifierOwner of the current class, used to retrieve the class name.
     */
    CURRENT_CLASS_NAME("currentClassName", "The name of the current class"),

    /**
     * Represents the input and output of PsiElement and PsiFile.
     */
    CURRENT_CLASS_CODE("currentClassCode", "The code of the current class"),

    CURRENT_METHOD_NAME("currentMethodName", "The name of the current method"),

    CURRENT_METHOD_CODE("currentMethodCode", "The code of the current method"),

    /**
     * Represents the input and output of PsiElement and PsiFile.
     */
    RELATED_CLASSES("relatedClasses", "The related classes based on the AST analysis"),

    /**
     * Uses TfIDF to search for similar test cases in the code.
     */
    SIMILAR_TEST_CASE("similarTestCase", "The similar test cases based on the TfIDF analysis"),

    /**
     * Represents the import statements required for the code structure.
     */
    IMPORTS("imports", "The import statements required for the code structure"),

    /**
     * Flag indicating whether the code structure is being generated in a new file.
     */
    IS_NEED_CREATE_FILE("isNeedCreateFile", "Flag indicating whether the code structure is being generated in a new file"),

    /**
     * The name of the target test file where the code structure will be generated.
     */
    TARGET_TEST_FILE_NAME("targetTestFileName", "The name of the target test file where the code structure will be generated"),

    /**
     * underTestMethod
     */
    UNDER_TEST_METHOD_CODE("underTestMethodCode", "The code of the method under test"),

    /**
     * Represents the framework information required for the code structure.
     */
    FRAMEWORK_CONTEXT("frameworkContext", "The framework information in dependencies of current project"),

    /**
     * codeSmell
     */
    CODE_SMELL("codeSmell", "Include psi error and warning"),
    ;

    companion object {
        /**
         * Returns the PsiVariable with the given variable name.
         *
         * @param variableName the variable name to search for
         * @return the PsiVariable with the given variable name
         */
        fun from(variableName: String): PsiContextVariable? {
            return values().firstOrNull { it.variableName == variableName }
        }
    }
}