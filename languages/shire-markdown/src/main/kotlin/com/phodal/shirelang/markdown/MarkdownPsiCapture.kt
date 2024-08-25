package com.phodal.shirelang.markdown

import com.phodal.shirecore.provider.psi.PsiCapture
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.MarkdownParser

class MarkdownPsiCapture: PsiCapture {
    private val embeddedHtmlType = IElementType("ROOT")

    /**
     * Capture markdown text with ast node Node
     */
    override fun capture(fileContent: String, type: String): List<String> {
        val flavour = GFMFlavourDescriptor()
        val parsedTree: ASTNode = MarkdownParser(flavour).parse(embeddedHtmlType, fileContent)

        val types: List<String> = when (type) {
            /**
             *  [GFMTokenTypes.GFM_AUTOLINK] , [MarkdownElementTypes.INLINE_LINK]
             */
            "link" -> listOf("GFM_AUTOLINK")
            else -> listOf()
        }

        // Traverse the AST to find and process nodes of the specified type
        val result = mutableListOf<String>()
        parsedTree.accept(object : RecursiveVisitor() {
            override fun visitNode(node: ASTNode) {
                when {
                    // ignore image
                    node.type == MarkdownElementTypes.IMAGE -> {
                        return
                    }
                    types.contains(node.type.name) -> {
                        result.add(fileContent.substring(node.startOffset, node.endOffset))
                    }

                    node.type.name.lowercase() == type -> {
                        result.add(fileContent.substring(node.startOffset, node.endOffset))
                    }
                }

                super.visitNode(node)
            }
        })

        return result.map { it.trim() }
    }
}