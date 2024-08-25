package com.phodal.shirecore.schema

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.SchemaType
import com.phodal.shirecore.ShireCoreBundle
import org.jetbrains.annotations.NonNls

@NonNls
internal const val SHIRE_ENV_PATTERN_EXTENSION = "shireEnv.json"

class ShireEnvFileProvider(project: Project) : JsonSchemaFileProvider {
    @NonNls
    private val DOT_EXTENSION = ".$SHIRE_ENV_PATTERN_EXTENSION"

    @NonNls
    private val SCHEMA = "/schemas/shireEnv.schema.json"

    override fun isAvailable(file: VirtualFile): Boolean = file.nameSequence.endsWith(DOT_EXTENSION)
    override fun getName(): String = ShireCoreBundle.message("schema.env.json.display.name")
    override fun getSchemaFile(): VirtualFile? = VfsUtil.findFileByURL(javaClass.getResource(SCHEMA)!!)
    override fun getSchemaType(): SchemaType = SchemaType.embeddedSchema
}


