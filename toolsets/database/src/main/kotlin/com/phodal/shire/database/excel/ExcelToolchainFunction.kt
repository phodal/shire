package com.phodal.shire.database.excel

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiBinaryFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.function.ToolchainFunctionProvider

class ExcelToolchainFunction : ToolchainFunctionProvider {
    override fun isApplicable(project: Project, funcName: String): Boolean {
        return funcName == "excel"
    }

    override fun execute(project: Project, funcName: String, args: List<Any>, allVariables: Map<String, Any?>): Any {
        return "excel"
    }

    /// markdown to excel
    fun markdownToExcel(project: Project, content: String): String {
        return "excel"
    }

    /// excel to markdown
    fun excelToMarkdown(project: Project, filePath: String): String? {
        val file = project.baseDir.findFileByRelativePath(filePath)
            ?: throw IllegalArgumentException("File not found: $filePath")
        val excelFile = PsiManager.getInstance(project).findFile(file)

        if (excelFile !is PsiBinaryFile) {
            throw IllegalArgumentException("File is not a binary file: $filePath")
        }

//        val config = BaseExtractorConfig()
//        val xlsxValuesExtractor = XlsxExtractorFactory().createExtractor(config)
//        xlsxValuesExtractor.startExtraction()
        return excelFile?.text
    }
}