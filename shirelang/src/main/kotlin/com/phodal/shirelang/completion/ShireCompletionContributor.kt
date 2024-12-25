package com.phodal.shirelang.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.IElementType
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand
import com.phodal.shirelang.completion.provider.*
import com.phodal.shirelang.psi.ShireFrontMatterEntry
import com.phodal.shirelang.psi.ShireTypes
import com.phodal.shirelang.psi.ShireUsed

class ShireCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(ShireTypes.LANGUAGE_IDENTIFIER),
            CodeFenceLanguageCompletion()
        )

        extend(CompletionType.BASIC, identifierAfter(ShireTypes.AGENT_START), CustomAgentCompletion())

        extend(CompletionType.BASIC, identifierAfter(ShireTypes.VARIABLE_START), VariableCompletionProvider())

        extend(CompletionType.BASIC, identifierAfter(ShireTypes.VARIABLE_START), AgentToolOverviewCompletion())

        extend(CompletionType.BASIC, identifierAfter(ShireTypes.COMMAND_START), BuiltinCommandCompletion())

        extend(CompletionType.BASIC, hobbitHoleKey(), HobbitHoleKeyCompletion())
        extend(CompletionType.BASIC, hobbitHolePattern(), HobbitHoleValueCompletion())

        extend(CompletionType.BASIC, identifierAfter(ShireTypes.PIPE), PostProcessorCompletion())

        extend(CompletionType.BASIC, whenConditionPattern(), WhenConditionCompletionProvider())
        extend(CompletionType.BASIC, whenConditionFuncPattern(), WhenConditionFunctionCompletionProvider())

        // command completion
        extend(
            CompletionType.BASIC,
            (valuePatterns(listOf(
                BuiltinCommand.FILE, BuiltinCommand.RUN, BuiltinCommand.WRITE, BuiltinCommand.STRUCTURE
            ))),
            FileReferenceLanguageProvider()
        )
        extend(
            CompletionType.BASIC,
            commandPropPattern(BuiltinCommand.REV.commandName),
            RevisionReferenceLanguageProvider()
        )
        extend(
            CompletionType.BASIC,
            commandPropPattern(BuiltinCommand.SYMBOL.commandName),
            SymbolReferenceLanguageProvider()
        )
        extend(
            CompletionType.BASIC,
            commandPropPattern(BuiltinCommand.FILE_FUNC.commandName),
            FileFunctionProvider()
        )
        extend(
            CompletionType.BASIC,
            commandPropPattern(BuiltinCommand.REFACTOR.commandName),
            RefactoringFuncProvider()
        )
        extend(
            CompletionType.BASIC,
            commandPropPattern(BuiltinCommand.RUN.commandName),
            ProjectRunProvider()
        )
    }

    private inline fun <reified I : PsiElement> psiElement() = PlatformPatterns.psiElement(I::class.java)

    private fun baseUsedPattern(): PsiElementPattern.Capture<PsiElement> =
        PlatformPatterns.psiElement()
            .inside(psiElement<ShireUsed>())

    private fun identifierAfter(type: IElementType): ElementPattern<out PsiElement> =
        PlatformPatterns.psiElement(ShireTypes.IDENTIFIER)
            .afterLeaf(PlatformPatterns.psiElement().withElementType(type))

    private fun commandPropPattern(text: String): PsiElementPattern.Capture<PsiElement> =
        baseUsedPattern()
            .withElementType(ShireTypes.COMMAND_PROP)
            .afterLeafSkipping(
                PlatformPatterns.psiElement(ShireTypes.COLON),
                PlatformPatterns.psiElement().withText(text)
            )

    private fun hobbitHolePattern(): ElementPattern<out PsiElement> {
        return PlatformPatterns.psiElement()
            .inside(psiElement<ShireFrontMatterEntry>())
            .afterLeafSkipping(
                PlatformPatterns.psiElement().withElementType(ShireTypes.FRONT_MATTER_KEY),
                PlatformPatterns.psiElement(ShireTypes.COLON)
            )
    }

    private fun whenConditionPattern(): ElementPattern<out PsiElement> {
        return PlatformPatterns.psiElement()
            .inside(psiElement<ShireFrontMatterEntry>())
            .afterLeaf(PlatformPatterns.psiElement().withText("$"))
    }

    private fun whenConditionFuncPattern(): ElementPattern<out PsiElement> {
        return PlatformPatterns.psiElement(ShireTypes.IDENTIFIER)
            .inside(psiElement<ShireFrontMatterEntry>())
            .afterLeafSkipping(
                PlatformPatterns.psiElement(ShireTypes.IDENTIFIER),
                PlatformPatterns.psiElement(ShireTypes.DOT),
            )
    }

    private fun hobbitHoleKey(): PsiElementPattern.Capture<PsiElement> {
        val excludedElements = listOf(
            ShireTypes.COLON,
            ShireTypes.DOT,
            ShireTypes.AGENT_START,
            ShireTypes.VARIABLE_START,
            ShireTypes.COMMAND_START
        ).map { PlatformPatterns.psiElement().afterLeaf(PlatformPatterns.psiElement(it)) }

        return excludedElements.fold(
            PlatformPatterns.psiElement(ShireTypes.IDENTIFIER)
        ) { pattern, excludedPattern ->
            pattern.andNot(excludedPattern)
        }
    }

    private fun valuePatterns(listOf: List<BuiltinCommand>): ElementPattern<out PsiElement> {
        val patterns = listOf.map { commandPropPattern(it.commandName) }
        return PlatformPatterns.or(*patterns.toTypedArray())
    }
}
