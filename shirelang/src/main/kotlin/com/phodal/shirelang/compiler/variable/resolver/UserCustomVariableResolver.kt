package com.phodal.shirelang.compiler.variable.resolver

import com.phodal.shirelang.compiler.hobbit.execute.PatternActionProcessor
import com.phodal.shirelang.compiler.variable.base.VariableResolver
import com.phodal.shirelang.compiler.variable.base.VariableResolverContext

class UserCustomVariableResolver(
    private val context: VariableResolverContext
) : VariableResolver {
    override fun resolve() : Map<String, String> {
        if (context.hole == null) {
            return emptyMap()
        }

        return context.hole.variables.mapValues {
            PatternActionProcessor(context.myProject, context.hole).execute(it.value)
        }
    }
}