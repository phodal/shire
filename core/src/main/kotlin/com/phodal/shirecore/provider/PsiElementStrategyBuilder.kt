package com.phodal.shirecore.provider

import com.intellij.lang.Language
import com.intellij.lang.LanguageExtension
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilBase
import com.phodal.shirecore.codemodel.model.ClassStructure

interface PsiElementStrategyBuilder {

    /**
     * Looks up a symbol in the given project by its canonical name.
     * The canonical name is the fully qualified name of the symbol, including the package name.
     * For example, the canonical name of the class `java.lang.String` is `java.lang.String`.
     * The canonical name of the method `java.lang.String#length` is `java.lang.String.length()`.
     *
     * @param project the project in which to search for the symbol
     * @param canonicalName the canonical name of the symbol to look up
     * @return the ClassStructure representing the symbol with the given canonical name, or null if not found
     */
    fun lookupSymbol(project: Project, canonicalName: String): ClassStructure? = null

    companion object {
        private val languageExtension: LanguageExtension<PsiElementStrategyBuilder> =
            LanguageExtension("com.phodal.shireElementStrategyBuilder")

        fun forLanguage(language: Language): PsiElementStrategyBuilder? {
            return languageExtension.forLanguage(language)
        }
    }
}

interface PsiElementStrategy {
    fun getElementToAction(project: Project?, editor: Editor?): PsiElement?
    fun getElementToAction(project: Project?, psiFile: PsiFile, range: TextRange): PsiElement?
}

class DefaultPsiElementStrategy: PsiElementStrategy {
    /**
     * Returns the PsiElement to explain in the given project and editor.
     *
     * @param project the project in which the element resides (nullable)
     * @param editor the editor in which the element is located (nullable)
     * @return the PsiElement to explain, or null if either the project or editor is null, or if no element is found
     */
    override fun getElementToAction(project: Project?, editor: Editor?): PsiElement? {
        if (project == null || editor == null) return null

        val element = PsiUtilBase.getElementAtCaret(editor) ?: return null
        val psiFile = element.containingFile

        if (InjectedLanguageManager.getInstance(project).isInjectedFragment(psiFile)) return psiFile

        val identifierOwner = PsiTreeUtil.getParentOfType(element, PsiNameIdentifierOwner::class.java)
        return identifierOwner ?: element
    }

    /**
     * This method calculates the frontend element to explain based on the given project, PsiFile, and TextRange.
     *
     * @param project the project to which the PsiFile belongs
     * @param psiFile the PsiFile in which the frontend element is located
     * @param range the TextRange specifying the range of the frontend element
     * @return the PsiElement representing the frontend element to explain, or null if the project is null, or the PsiFile is invalid
     */
    override fun getElementToAction(project: Project?, psiFile: PsiFile, range: TextRange): PsiElement? {
        if (project == null || !psiFile.isValid) return null

        val element = PsiUtilBase.getElementAtOffset(psiFile, range.startOffset)
        if (InjectedLanguageManager.getInstance(project).isInjectedFragment(psiFile)) {
            return psiFile
        }

        val injected = InjectedLanguageManager.getInstance(project).findInjectedElementAt(psiFile, range.startOffset)
        if (injected != null) {
            return injected.containingFile
        }

        val psiElement: PsiElement? = PsiTreeUtil.getParentOfType(element, PsiNameIdentifierOwner::class.java)
        return psiElement ?: element
    }
}