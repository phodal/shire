package com.phodal.shirecore.provider.impl

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.MarkdownParser

private val embeddedHtmlType = IElementType("ROOT")

class MarkdownPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language?.id?.lowercase() != "markdown") return ""

        val markdownText = psiElement.text

        return when (variable) {
            PsiContextVariable.STRUCTURE -> {
                toHtml(markdownText)
            }

            else -> ""
        }
    }

    fun toHtml(markdownText: @NlsSafe String): String {
        val flavour = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).parse(embeddedHtmlType, markdownText)
        val markdownNode = MarkdownNode(parsedTree, null, markdownText)
        return markdownNode.toHtml()
    }

    private fun MarkdownNode.toHtml(): String {
        if (node.type == MarkdownTokenTypes.WHITE_SPACE) {
            return text   // do not trim trailing whitespace
        }

        val sb = StringBuilder()
        visit { node, processChildren ->
            fun wrapChildren(tag: String, level: Int = 0) {
                sb.append("#".repeat(level))
                processChildren()
                sb.append("\n")
            }

            val nodeType = node.type
            val nodeText = node.text

            when (nodeType) {
                MarkdownElementTypes.ATX_1 -> wrapChildren("h1", 1)
                MarkdownElementTypes.ATX_2 -> wrapChildren("h2" ,2)
                MarkdownElementTypes.ATX_3 -> wrapChildren("h3", 3)
                MarkdownElementTypes.ATX_4 -> wrapChildren("h4", 4)
                MarkdownElementTypes.ATX_5 -> wrapChildren("h5", 5)
                MarkdownElementTypes.ATX_6 -> wrapChildren("h6", 6)

                MarkdownTokenTypes.TEXT,
                MarkdownTokenTypes.WHITE_SPACE,
                MarkdownTokenTypes.COLON,
                MarkdownTokenTypes.SINGLE_QUOTE,
                MarkdownTokenTypes.DOUBLE_QUOTE,
                MarkdownTokenTypes.LPAREN,
                MarkdownTokenTypes.RPAREN,
                MarkdownTokenTypes.LBRACKET,
                MarkdownTokenTypes.RBRACKET,
                MarkdownTokenTypes.EXCLAMATION_MARK,
                GFMTokenTypes.CHECK_BOX,
                GFMTokenTypes.GFM_AUTOLINK -> {
                    sb.append(nodeText)
                }

                MarkdownTokenTypes.ATX_HEADER,
                MarkdownTokenTypes.ATX_CONTENT -> {
                    processChildren()
                }

                else -> {
                    if (nodeType.name == "ROOT") {
                        processChildren()
                    }

//                    processChildren()
                }
            }
        }

        return sb.toString().trimEnd()
    }


    class MarkdownNode(val node: ASTNode, val parent: MarkdownNode?, val content: String) {
        val children: List<MarkdownNode> = node.children.map { MarkdownNode(it, this, content) }
        val startOffset: Int get() = node.startOffset
        val type: IElementType get() = node.type
        var text: String = content.substring(node.startOffset, node.endOffset)
        fun child(type: IElementType): MarkdownNode? = children.firstOrNull { it.type == type }
    }

    private fun MarkdownNode.visit(action: (MarkdownNode, () -> Unit) -> Unit) {
        action(this) {
            for (child in children) {
                child.visit(action)
            }
        }
    }
}