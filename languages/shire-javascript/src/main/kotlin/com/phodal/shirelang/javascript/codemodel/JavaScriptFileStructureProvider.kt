package com.phodal.shirelang.javascript.codemodel

import com.intellij.lang.ecmascript6.psi.impl.ES6ImportPsiUtil
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.phodal.shirecore.provider.codemodel.FileStructureProvider
import com.phodal.shirecore.provider.codemodel.model.FileStructure

class JavaScriptFileStructureProvider : FileStructureProvider {
    override fun build(psiFile: PsiFile): FileStructure? {
        val file = psiFile.virtualFile ?: return null
        val importDeclarations = ES6ImportPsiUtil.getImportDeclarations((psiFile as PsiElement))
        val classes =
            PsiTreeUtil.getChildrenOfTypeAsList(psiFile as PsiElement, JSClass::class.java)
        val functions =
            PsiTreeUtil.getChildrenOfTypeAsList(psiFile as PsiElement, JSFunction::class.java)

        return FileStructure(
            psiFile,
            psiFile.name,
            file.path,
            null,
            importDeclarations,
            classes,
            functions
        )
    }
}