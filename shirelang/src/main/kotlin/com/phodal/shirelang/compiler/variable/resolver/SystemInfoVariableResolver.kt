package com.phodal.shirelang.compiler.variable.resolver

import com.phodal.shirecore.provider.variable.model.SystemInfoVariable
import com.phodal.shirelang.compiler.variable.base.VariableResolver
import com.phodal.shirelang.compiler.variable.base.VariableResolverContext

/**
 * SystemInfoVariableResolver is a class that provides a way to resolve system information variables.
 */
class SystemInfoVariableResolver(
    private val context: VariableResolverContext,
) : VariableResolver {
    override fun resolve(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        SystemInfoVariable.all().forEach {
            result[it.variableName] = it.value!!
        }

        return result
    }
}