package com.phodal.shirelang.actions.validator

import com.intellij.psi.PsiFile
import com.phodal.shirelang.compiler.hobbit.FrontMatterType
import com.phodal.shirelang.compiler.hobbit.Statement

/**
 * Intention variable for condition include:
 * - filePath
 * - fileName
 * - fileType
 * - fileExtension
 * - fileContent
 */
data class PsiVariables(
    val filePath: String,
    val fileName: String,
    val fileType: String,
    val fileExtension: String,
    val fileContent: String,
) {
    companion object {
        fun fromPsiFile(file: PsiFile): PsiVariables {
            return PsiVariables(
                file.virtualFile.path,
                file.name,
                file.fileType.name,
                file.virtualFile.extension ?: "",
                file.text
            )
        }

        fun completionProvider(): Map<String, String> {
            return mapOf(
                "filePath" to "The path of the file",
                "fileName" to "The name of the file",
                "fileType" to "The type of the file",
                "fileExtension" to "The extension of the file",
                "fileContent" to "The content of the file"
            )
        }
    }
}

class WhenConditionValidator(
    private val psiVariables: PsiVariables
) {

    fun variables(): Map<String, String> {
        return mapOf(
            "filePath" to psiVariables.filePath,
            "fileName" to psiVariables.fileName,
            "fileType" to psiVariables.fileType,
            "fileExtension" to psiVariables.fileExtension,
            "fileContent" to psiVariables.fileContent
        )
    }

    companion object {
        fun build(file: PsiFile): WhenConditionValidator {
            return WhenConditionValidator(PsiVariables.fromPsiFile(file))
        }

        fun isAvailable(conditions: FrontMatterType.Expression, file: PsiFile): Boolean {
            val variables: Map<String, String> = build(file).variables()
            return (conditions.value as Statement).evaluate(variables) == true
        }
    }

}
