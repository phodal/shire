package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.psi.RelatedClassesProvider
import com.phodal.shirecore.provider.shire.ShireSymbolProvider
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

class RelatedSymbolInsCommand(val myProject: Project, private val symbol: String) : ShireCommand {
    override val commandName = BuiltinCommand.RELATED

    override suspend fun doExecute(): String? {
        val elements = ShireSymbolProvider.all().map {
            it.resolveSymbol(myProject, symbol)
        }.flatten()

        if (elements.isEmpty()) return null

        val psiElements = elements.mapNotNull {
            RelatedClassesProvider.provide(it.language)?.lookup(it)
        }.flatten()

        if (psiElements.isEmpty()) return null

        return psiElements.joinToString("\n") { it.text }
    }
}
