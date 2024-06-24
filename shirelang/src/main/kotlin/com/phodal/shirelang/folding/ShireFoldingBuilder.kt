package com.phodal.shirelang.folding

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilCore
import com.intellij.psi.util.elementType
import com.phodal.shirelang.psi.*

class ShireFoldingBuilder : FoldingBuilderEx() {
    override fun isCollapsedByDefault(node: ASTNode): Boolean = true
    override fun getPlaceholderText(node: ASTNode): String = node.text

    override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()
        root.accept(ShireFoldingVisitor(descriptors))

        return descriptors.toTypedArray()
    }

    override fun getPlaceholderText(node: ASTNode, range: TextRange): String? {
        val elementType = PsiUtilCore.getElementType(node)
        val explicitName = foldedElementsPresentations[elementType]
        val elementText = StringUtil.shortenTextWithEllipsis(node.text, 30, 5)
        return explicitName?.let { "$it: $elementText" } ?: elementText
    }

    private val foldedElementsPresentations = hashMapOf(
        ShireTypes.FRONT_MATTER_HEADER to "Hobbit Hole",
        ShireTypes.CODE to "Code Block",
        ShireTypes.QUERY_STATEMENT to "Shire AstQL",
        ShireTypes.BLOCK_COMMENT to "/* ... */",
    )

    override fun isCollapsedByDefault(foldingDescriptor: FoldingDescriptor): Boolean {
        return when (foldingDescriptor.element.elementType) {
            ShireTypes.FRONT_MATTER_HEADER -> true
            ShireTypes.CODE -> false
            else -> false
        }
    }
}

class ShireFoldingVisitor(private val descriptors: MutableList<FoldingDescriptor>) : ShireVisitor() {
    override fun visitElement(element: PsiElement) {
        when (element.elementType) {
            ShireTypes.FRONT_MATTER_HEADER -> {
                descriptors.add(FoldingDescriptor(element.node, element.textRange))
            }

            ShireTypes.CODE -> {
                descriptors.add(FoldingDescriptor(element.node, element.textRange))
            }
        }
        element.acceptChildren(this)
    }

    override fun visitQueryStatement(o: ShireQueryStatement) {
        descriptors.add(FoldingDescriptor(o.node, o.textRange))
    }

    override fun visitCaseBody(o: ShireCaseBody) {
        descriptors.add(FoldingDescriptor(o.node, o.textRange))
    }
}
