package com.phodal.shirelang.proto.variable

import com.intellij.openapi.project.Project
import com.intellij.protobuf.lang.ProtoBaseLanguage
import com.intellij.protobuf.lang.psi.PbFile
import com.phodal.shirecore.provider.context.LanguageToolchainProvider
import com.phodal.shirecore.provider.context.ToolchainContextItem
import com.phodal.shirecore.provider.context.ToolchainPrepareContext

class ProtobufToolchainProvider : LanguageToolchainProvider {
    override fun isApplicable(project: Project, context: ToolchainPrepareContext): Boolean {
        return context.sourceFile?.language is ProtoBaseLanguage
    }

    override suspend fun collect(project: Project, context: ToolchainPrepareContext): List<ToolchainContextItem> {
        val file = context.sourceFile as? PbFile ?: return emptyList()
        val protoVersion = file.syntaxLevel

        return listOf(
            ToolchainContextItem(
                ProtobufToolchainProvider::class,
                "- Protobuf version: $protoVersion"
            )
        )
    }
}
