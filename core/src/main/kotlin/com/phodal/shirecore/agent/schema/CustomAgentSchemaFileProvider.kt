package com.phodal.shirecore.agent.schema

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.SchemaType
import com.phodal.shirecore.ShireCoreBundle
import org.jetbrains.annotations.NonNls

@NonNls
internal const val CUSTOM_AGENT_JSON_EXTENSION = "shireCustomAgent.json"

class CustomAgentSchemaFileProvider(project: Project) : JsonSchemaFileProvider {
    @NonNls
    private val DOT_EXTENSION = ".$CUSTOM_AGENT_JSON_EXTENSION"

    @NonNls
    private val CUSTOM_AGENT_SCHEMA = "/schemas/shireCustomAgent.schema.json"

    override fun isAvailable(file: VirtualFile): Boolean = file.nameSequence.endsWith(DOT_EXTENSION)
    override fun getName(): String = ShireCoreBundle.message("schema.custom-agent.json.display.name")
    override fun getSchemaFile(): VirtualFile? = VfsUtil.findFileByURL(javaClass.getResource(CUSTOM_AGENT_SCHEMA)!!)
    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}
