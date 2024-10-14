package com.phodal.shirelang.compiler.command

import com.phodal.shirelang.compiler.parser.SHIRE_ERROR
import com.intellij.openapi.project.Project
import com.phodal.shirecore.provider.shire.ShireSymbolProvider

class SymbolShireCommand(val myProject: Project, private val prop: String) :
    ShireCommand {
    override suspend fun doExecute(): String {
        val result = ShireSymbolProvider.all().mapNotNull {
            val found = it.resolveSymbol(myProject, prop)
            if (found.isEmpty()) return@mapNotNull null
            "```${it.language}\n${found.joinToString("\n")}\n```\n"
        }

        if (result.isEmpty()) {
            return "$SHIRE_ERROR No symbol found: $prop"
        }

        return result.joinToString("\n")
    }
}