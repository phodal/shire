package com.phodal.shirecore.codemodel.model

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.phodal.shirecore.codemodel.ClassStructureProvider
import com.phodal.shirecore.codemodel.MethodStructureProvider
import com.phodal.shirecore.codemodel.base.FormatableElement

class VariableStructure(
    override val root: PsiElement,
    override val text: String,
    override val name: String?,
    private val enclosingMethod: PsiElement? = null,
    private val enclosingClass: PsiElement?= null,
    private val usages: List<PsiReference> = emptyList(),
    private val includeMethodContext: Boolean = false,
    private val includeClassContext: Boolean = false
) : FormatableElement(root, text, name) {
    private val methodContext: MethodStructure? = if (includeMethodContext && enclosingMethod != null) {
        MethodStructureProvider.from(enclosingMethod, false, false)
    } else {
        null
    }
    private val classContext: ClassStructure? = if (includeClassContext && enclosingClass != null) {
        ClassStructureProvider.from(enclosingClass, false)
    } else {
        null
    }

    fun shortFormat(): String = runReadAction {  root.text ?: ""}

    /**
     * Returns a formatted string representation of the method.
     *
     * The returned string includes the following information:
     * - The name of the method, or "_" if the name is null.
     * - The name of the method's context, or "_" if the context is null.
     * - The name of the class's context, or "_" if the context is null.
     *
     * @return A formatted string representation of the method.
     */
    override fun format(): String {
        return """
            var name: ${name ?: "_"}
            var method name: ${methodContext?.name ?: "_"}
            var class name: ${classContext?.name ?: "_"}
        """.trimIndent()
    }
}