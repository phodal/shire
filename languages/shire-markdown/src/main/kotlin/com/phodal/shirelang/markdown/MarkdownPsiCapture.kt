package com.phodal.shirelang.markdown

import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.accept
import org.intellij.markdown.ast.visitors.RecursiveVisitor
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMTokenTypes
import org.intellij.markdown.parser.MarkdownParser

class MarkdownPsiCapture {
    private val embeddedHtmlType = IElementType("ROOT")

    /**
     * Capture markdown text with ast node Node
     */
    fun captureUrl(markdownText: String, type: String): List<String> {
        val flavour = GFMFlavourDescriptor()
        val parsedTree: ASTNode = MarkdownParser(flavour).parse(embeddedHtmlType, markdownText)

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
                    types.contains(node.type.name) -> {
                        result.add(markdownText.substring(node.startOffset, node.endOffset))
                    }

                    node.type.name.lowercase() == type -> {
                        result.add(markdownText.substring(node.startOffset, node.endOffset))
                    }
                }

                super.visitNode(node)
            }
        })

        return result.map { it.trim() }
    }


    class MarkdownNode(val node: ASTNode, val parent: MarkdownNode?, val rootText: String) {
        val children: List<MarkdownNode> = node.children.map { MarkdownNode(it, this, rootText) }
        val endOffset: Int get() = node.endOffset
        val startOffset: Int get() = node.startOffset
        val type: IElementType get() = node.type
        val text: String get() = rootText.substring(startOffset, endOffset)
        fun child(type: IElementType): MarkdownNode? = children.firstOrNull { it.type == type }
    }

    private fun MarkdownNode.capture(): MutableList<String> {
        val result = mutableListOf<String>()
        visit { node, processChildren ->
            fun wrapChildren(tag: String, newline: Boolean = false) {
                processChildren()
            }

            val nodeType = node.type
            val nodeText = node.text
            when (nodeType) {
                MarkdownElementTypes.UNORDERED_LIST -> wrapChildren("ul", newline = true)
                MarkdownElementTypes.ORDERED_LIST -> wrapChildren("ol", newline = true)
                MarkdownElementTypes.LIST_ITEM -> wrapChildren("li")
                MarkdownElementTypes.EMPH -> wrapChildren("em")
                MarkdownElementTypes.STRONG -> wrapChildren("strong")
                GFMElementTypes.STRIKETHROUGH -> wrapChildren("del")
                MarkdownElementTypes.ATX_1 -> wrapChildren("h1")
                MarkdownElementTypes.ATX_2 -> wrapChildren("h2")
                MarkdownElementTypes.ATX_3 -> wrapChildren("h3")
                MarkdownElementTypes.ATX_4 -> wrapChildren("h4")
                MarkdownElementTypes.ATX_5 -> wrapChildren("h5")
                MarkdownElementTypes.ATX_6 -> wrapChildren("h6")
                MarkdownElementTypes.BLOCK_QUOTE -> wrapChildren("blockquote")
                MarkdownElementTypes.PARAGRAPH -> wrapChildren("p", newline = true)

                MarkdownElementTypes.CODE_SPAN,
                MarkdownElementTypes.CODE_BLOCK,
                MarkdownElementTypes.CODE_FENCE,
                MarkdownTokenTypes.FENCE_LANG,
                MarkdownTokenTypes.CODE_LINE,
                MarkdownTokenTypes.CODE_FENCE_CONTENT -> {
                    // skip
                }

                MarkdownElementTypes.SHORT_REFERENCE_LINK,
                MarkdownElementTypes.FULL_REFERENCE_LINK -> {
                    val linkLabelNode = node.child(MarkdownElementTypes.LINK_LABEL)
                    val linkLabelContent = linkLabelNode?.children
                        ?.dropWhile { it.type == MarkdownTokenTypes.LBRACKET }
                        ?.dropLastWhile { it.type == MarkdownTokenTypes.RBRACKET }
                    if (linkLabelContent != null) {
                        val label = linkLabelContent.joinToString(separator = "") { it.text }
                        val linkText = node.child(MarkdownElementTypes.LINK_TEXT)?.text ?: label
                        result.add(linkText)
                    }
                }

                MarkdownElementTypes.INLINE_LINK -> {
//                    val destination = node.child(MarkdownElementTypes.LINK_DESTINATION)?.text
//                    result.add(destination ?: "")
                }

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
                    result.add(nodeText)
                }
                MarkdownTokenTypes.EOL -> {}
                MarkdownTokenTypes.GT -> {}
                MarkdownTokenTypes.LT -> {}
                MarkdownElementTypes.LINK_TEXT -> {}
                MarkdownTokenTypes.EMPH -> {}
                GFMTokenTypes.TILDE -> {}
                GFMElementTypes.TABLE -> {}

                else -> {
                    processChildren()
                }
            }
        }

        return result
    }

    private fun MarkdownNode.visit(action: (MarkdownNode, () -> Unit) -> Unit) {
        action(this) {
            for (child in children) {
                child.visit(action)
            }
        }
    }
}