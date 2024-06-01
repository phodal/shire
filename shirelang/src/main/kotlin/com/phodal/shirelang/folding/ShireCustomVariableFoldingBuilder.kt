package com.phodal.shirelang.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.elementType
import com.phodal.shirelang.psi.ShireTypes

class ShireCustomVariableFoldingBuilder : FoldingBuilderEx() {
    override fun isCollapsedByDefault(node: ASTNode): Boolean = true
    override fun getPlaceholderText(node: ASTNode): String = node.text

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        root.accept(object : PsiElementVisitor() {
            override fun visitElement(element: PsiElement) {
                if (element.elementType == ShireTypes.VARIABLE_ID) {
                    descriptors.add(FoldingDescriptor(element.node, element.textRange))
                }
                element.acceptChildren(this)
            }
        })

        return descriptors.toTypedArray()
    }
}
