package com.phodal.shirelang.compiler.variable.resolver

import com.intellij.openapi.diagnostic.logger
import com.phodal.shirecore.provider.variable.*
import com.phodal.shirecore.provider.variable.model.ToolchainVariable
import com.phodal.shirelang.compiler.variable.base.VariableResolver
import com.phodal.shirelang.compiler.variable.base.VariableResolverContext

/**
 * Include ToolchainVariableProvider and PsiContextVariableProvider
 */
class ToolchainVariableResolver(
    private val context: VariableResolverContext,
) : VariableResolver {
    override suspend fun resolve(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        context.variableTable.getAllVariables().forEach {
            val variable = ToolchainVariable.from(it.key) ?: return@forEach
            val provider = ToolchainVariableProvider.provide(variable, context.element) ?: return@forEach

            result[it.key] = try {
                val resolvedValue = provider.resolve(variable, context.myProject, context.editor, context.element)
                val value = (resolvedValue as? ToolchainVariable)?.value ?: resolvedValue
                logger<ToolchainVariableResolver>().info("start to resolve variable: $value")
                value
            } catch (e: Exception) {
                logger<ToolchainVariableResolver>().error("Failed to resolve variable: ${it.key}", e)
                ""
            }

        }

        return result
    }
}