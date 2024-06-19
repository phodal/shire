package com.phodal.shirelang.actions.validator

import com.intellij.psi.PsiFile
import com.phodal.shirelang.compiler.hobbit.ast.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.ast.Statement

/**
 * Intention variable for condition include:
 * - filePath
 * - fileName
 * - fileExtension
 * - fileContent
 */
data class PsiVariables(
    val filePath: String,
    val fileName: String,
    val fileExtension: String,
    val fileContent: String,
) {
    companion object {
        fun fromPsiFile(file: PsiFile): PsiVariables {
            return PsiVariables(
                file.virtualFile.path,
                file.name,
                file.virtualFile.extension ?: "",
                file.text
            )
        }


        /**
         * Design for code completion provider
         */
        fun completionProvider(): Map<String, String> {
            return mapOf(
                "filePath" to "The path of the file",
                "fileName" to "The name of the file",
                "fileExtension" to "The extension of the file",
                "fileContent" to "The content of the file"
            )
        }
    }
}

class WhenConditionValidator(
    private val psiVariables: PsiVariables
) {
    fun buildPsiVariable(): Map<String, String> {
        return mapOf(
            "filePath" to psiVariables.filePath,
            "fileName" to psiVariables.fileName,
            "fileExtension" to psiVariables.fileExtension,
            "fileContent" to psiVariables.fileContent
        )
    }

    companion object {
        fun build(file: PsiFile): WhenConditionValidator {
            return WhenConditionValidator(PsiVariables.fromPsiFile(file))
        }

        fun isAvailable(conditions: FrontMatterType.Expression, file: PsiFile): Boolean {
            val variables: Map<String, String> = build(file).buildPsiVariable()
            return (conditions.value as Statement).evaluate(variables) == true
        }
    }

}
