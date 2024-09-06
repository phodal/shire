package com.phodal.shirelang.navigation

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase
import com.intellij.openapi.application.runInEdt
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.util.elementType
import com.phodal.shirecore.middleware.builtin.findFile
import com.phodal.shirelang.psi.ShireFuncCall
import com.phodal.shirelang.psi.ShireTypes
import com.phodal.shirelang.utils.lookupFile

class ShireGotoDeclarationHandler : GotoDeclarationHandlerBase(), GotoDeclarationHandler {
    override fun getGotoDeclarationTarget(element: PsiElement?, editor: Editor?): PsiElement? {
        if (element !is LeafPsiElement) return null
        if (element.elementType != ShireTypes.QUOTE_STRING) return null
        if (element.parent?.elementType != ShireTypes.PIPELINE_ARG) return null

        val project = element.project

        val funcCall = element.parent?.parent?.parent as? ShireFuncCall ?: return null
        val funcName = funcCall.funcName.text

        if (funcName != "execute" && funcName != "thread") return null

        val fileName = element.text.removeSurrounding("\"")
        val file = project.lookupFile(fileName) ?: project.findFile(fileName) ?: return null

        runInEdt {
            FileEditorManager.getInstance(project).openFile(file, true)
        }
        return null
    }
}
