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
class WhenConditionValidator(
    private val filePath: String,
    private val fileName: String,
    private val fileType: String,
    private val fileExtension: String,
    private val fileContent: String
) {

    fun variables(): Map<String, String> {
        return mapOf(
            "filePath" to filePath,
            "fileName" to fileName,
            "fileType" to fileType,
            "fileExtension" to fileExtension,
            "fileContent" to fileContent
        )
    }

    companion object {
        fun build(file: PsiFile): WhenConditionValidator {
            return WhenConditionValidator(
                file.virtualFile.path,
                file.name,
                file.fileType.name,
                file.fileType.defaultExtension,
                file.text
            )
        }

        fun isAvailable(conditions: FrontMatterType.Expression, file: PsiFile): Boolean {
            val variables: Map<String, String> = build(file).variables()
            return (conditions.value as Statement).evaluate(variables) == true
        }
    }

}
