package com.phodal.shirecore.agent.schema

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.SchemaType
import com.phodal.shirecore.ShireCoreBundle
import org.jetbrains.annotations.NonNls

@NonNls
internal const val SECRET_PATTERN_EXTENSION = "shireSecretPattern.yml"

class SecretPatternSchemaFileProvider(project: Project) : JsonSchemaFileProvider {
    @NonNls
    private val DOT_EXTENSION = ".$SECRET_PATTERN_EXTENSION"

    @NonNls
    private val SCHEMA = "/schemas/shireSecretPattern.schema.json"

    override fun isAvailable(file: VirtualFile): Boolean = file.nameSequence.endsWith(DOT_EXTENSION)
    override fun getName(): String = ShireCoreBundle.message("schema.pattern.json.display.name")
    override fun getSchemaFile(): VirtualFile? = VfsUtil.findFileByURL(javaClass.getResource(SCHEMA)!!)
    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}
