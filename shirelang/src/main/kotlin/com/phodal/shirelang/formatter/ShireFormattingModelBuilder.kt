// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.phodal.shirelang.formatter

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.DocumentBasedFormattingModel
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.templateLanguages.OuterLanguageElement
import com.intellij.psi.tree.IElementType
import com.intellij.psi.util.PsiUtilCore
import com.intellij.util.SmartList
import com.intellij.util.containers.FactoryMap
import com.phodal.shirelang.psi.ShireElementType
import com.phodal.shirelang.psi.ShireTypes

class ShireFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val file = formattingContext.containingFile
        val settings = formattingContext.codeStyleSettings

        val rootBlock = createBlock(ShireFormattingContext(settings, file), formattingContext.node)
        return DocumentBasedFormattingModel(rootBlock, settings, file)
    }

    companion object {
        fun createBlock(context: ShireFormattingContext, node: ASTNode): Block {
            val nodeType = PsiUtilCore.getElementType(node)
            return ShireFormattingBlock(context, node)
        }
    }
}

class ShireFormattingBlock(private val myContext: ShireFormattingContext, val myNode: ASTNode) :
    AbstractBlock(myNode, null, myContext.computeAlignment(myNode)) {
    private val myIndent: Indent?

    init {
        myIndent = myContext.computeBlockIndent(myNode)
    }

    override fun getIndent(): Indent? {
        return myIndent
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? = myContext.computeSpacing(this, child1, child2)
    override fun isLeaf(): Boolean = false
    override fun buildChildren(): List<Block> = buildSubBlocks(myContext, myNode)

    private fun buildSubBlocks(context: ShireFormattingContext, node: ASTNode): List<Block> {
        val res: MutableList<Block> = SmartList()
        var subNode = node.firstChildNode
        while (subNode != null) {
            val subNodeType = PsiUtilCore.getElementType(subNode)
            if (ShireElementType.SPACE_ELEMENTS.contains(subNodeType)) {
                // just skip them (comment processed above)
            } else if (ShireTypes.QUOTE_STRING === subNodeType) {
                res.addAll(buildSubBlocks(context, subNode))
            } else if (ShireElementType.CONTAINERS.contains(subNodeType)) {
                res.addAll(
                    substituteInjectedBlocks(
                        context.mySettings,
                        buildSubBlocks(context, subNode),
                        subNode, wrap, context.computeAlignment(subNode)
                    )
                )
            } else {
                res.add(ShireFormattingModelBuilder.createBlock(context, subNode))
            }
            subNode = subNode.treeNext
        }
        return res
    }

    internal fun substituteInjectedBlocks(
        settings: CodeStyleSettings,
        rawSubBlocks: List<Block>,
        injectionHost: ASTNode,
        wrap: Wrap?,
        alignment: Alignment?,
    ): List<Block> {
        val injectedBlocks = SmartList<Block>().apply {
            val outerBLocks = rawSubBlocks.filter { (it as? ASTBlock)?.node is OuterLanguageElement }
            val fixedIndent =
                IndentImpl(Indent.Type.SPACES, false, 2, false, false)
//            YamlInjectedLanguageBlockBuilder(settings, outerBLocks).addInjectedBlocks(this, injectionHost, wrap, alignment, fixedIndent)
        }
        if (injectedBlocks.isEmpty()) return rawSubBlocks

        injectedBlocks.addAll(
            0,
            rawSubBlocks.filter(injectedBlocks.first().textRange.startOffset.let { start -> { it.textRange.endOffset <= start } })
        )
        injectedBlocks.addAll(rawSubBlocks.filter(injectedBlocks.last().textRange.endOffset.let { end -> { it.textRange.startOffset >= end } }))

        return injectedBlocks
    }
}

class ShireFormattingContext(val mySettings: CodeStyleSettings, file: PsiFile) {
    private val myChildIndentAlignments: Map<ASTNode, Alignment> = FactoryMap.create { node: ASTNode? ->
        Alignment.createAlignment(true)
    }
    private val myChildValueAlignments: Map<ASTNode, Alignment> = FactoryMap.create { node: ASTNode? ->
        Alignment.createAlignment(true)
    }

    fun computeAlignment(node: ASTNode): Alignment? {
        val type: IElementType = PsiUtilCore.getElementType(node)
        if (type === ShireTypes.COLON) {
            return myChildValueAlignments[node.treeParent.treeParent]
        }
        if (type === ShireTypes.KEY_VALUE) {
            return myChildIndentAlignments[node.treeParent]
        }

        return null
    }

    fun computeSpacing(shireFormattingBlock: ShireFormattingBlock, child1: Block?, child2: Block): Spacing? {
        return null
    }

    private val DIRECT_NORMAL_INDENT: Indent = Indent.getNormalIndent(true)
    private val SAME_AS_PARENT_INDENT: Indent = Indent.getSpaceIndent(0, true)
    private val SAME_AS_INDENTED_ANCESTOR_INDENT: Indent = Indent.getSpaceIndent(0)

    fun computeBlockIndent(node: ASTNode): Indent? {
        val nodeType: IElementType = PsiUtilCore.getElementType(node) ?: return null

        if (nodeType === ShireTypes.KEY_VALUE) {
            return computeKeyValuePairIndent(node)
        }

        return null
    }

    private fun computeKeyValuePairIndent(node: ASTNode): Indent? {
        val parentType = PsiUtilCore.getElementType(node.treeParent)
        val grandParentType = if (parentType == null) null else PsiUtilCore.getElementType(node.treeParent.treeParent)

        return DIRECT_NORMAL_INDENT
    }
}
