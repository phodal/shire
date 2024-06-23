package com.phodal.shirelang.impl

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.phodal.shirecore.provider.shire.ShireSymbolProvider

class DefaultShireSymbolProvider : ShireSymbolProvider {
    override val language: String = "Default"

    override fun lookupSymbol(
        project: Project,
        parameters: CompletionParameters,
        result: CompletionResultSet,
    ): List<LookupElement> {
        return emptyList()
    }

    override fun lookupElementByName(project: Project, name: String): List<PsiElement>? {
        val searchScope = ProjectScope.getProjectScope(project)
        val virtualFiles = FileTypeIndex.getFiles(PlainTextFileType.INSTANCE, searchScope)

        return when (name) {
            "String" -> {
                return emptyList()
            }

            "File" -> {
                return virtualFiles.mapNotNull { PsiManager.getInstance(project).findFile(it) }.toList()
            }

            else -> {
                return emptyList()
            }
        }
    }

    override fun resolveSymbol(project: Project, symbol: String): List<String> {
        return emptyList()
    }
}