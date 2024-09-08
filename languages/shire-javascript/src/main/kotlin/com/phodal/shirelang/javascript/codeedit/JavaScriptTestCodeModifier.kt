package com.phodal.shirelang.javascript.codeedit

import com.intellij.lang.Language
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.lang.javascript.psi.impl.JSPsiElementFactory
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.codeedit.CodeModifier
import com.phodal.shirelang.javascript.util.LanguageApplicableUtil

open class JavaScriptTestCodeModifier : CodeModifier {
    override fun isApplicable(language: Language): Boolean {
        return LanguageApplicableUtil.isJavaScriptApplicable(language)
    }

    override fun smartInsert(
        sourceFile: VirtualFile,
        project: Project,
        code: String
    ): PsiElement? {
        TODO("Not yet implemented")
    }

    override fun insertTestCode(sourceFile: VirtualFile, project: Project, code: String): PsiElement? {
        val isExit = sourceFile as? JSFile
        if (isExit == null) {
            return insertClass(sourceFile, project, code)
        }

        return insertMethod(sourceFile, project, code)
    }

    override fun insertMethod(sourceFile: VirtualFile, project: Project, code: String): PsiElement? {
        // todo: spike for insert different method type, like named function, arrow function, etc.
        val jsFile = PsiManager.getInstance(project).findFile(sourceFile) as JSFile
        val psiElement = jsFile.lastChild

        val element = PsiFileFactory.getInstance(project).createFileFromText(jsFile.language, "")
        val codeElement = JSPsiElementFactory.createJSStatement(code, element)

        return runReadAction {
            psiElement?.parent?.addAfter(codeElement, psiElement)
        }
    }

    override fun insertClass(sourceFile: VirtualFile, project: Project, code: String): PsiElement? {
        return WriteCommandAction.runWriteCommandAction<PsiElement?>(project) {
            val psiFile = PsiManager.getInstance(project).findFile(sourceFile) as JSFile
            val document = psiFile.viewProvider.document!!
            document.insertString(document.textLength, code)
            psiFile.lastChild
        }
    }

}
