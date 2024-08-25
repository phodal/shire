package com.phodal.shirecore.schema

import com.intellij.openapi.project.Project
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory

class ShireJsonSchemaProviderFactory : JsonSchemaProviderFactory {
    override fun getProviders(project: Project): MutableList<JsonSchemaFileProvider> {
        return mutableListOf(
            ShireSecretPatternSchemaFileProvider(project),
            ShireCustomAgentSchemaFileProvider(project),
            ShireEnvFileProvider(project)
        )
    }
}
