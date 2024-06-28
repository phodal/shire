package com.phodal.shirelang.compiler.variable

import com.phodal.shirelang.compiler.hobbit.execute.PatternActionProcessor
import com.phodal.shirelang.compiler.variable._base.VariableResolver
import com.phodal.shirelang.compiler.variable._base.VariableResolverContext

class UserCustomVariableResolver(
    private val context: VariableResolverContext
) : VariableResolver {
    override fun resolve() : Map<String, String> {
        if (context.hole == null) {
            return emptyMap()
        }

        return context.hole.variables.mapValues {
            PatternActionProcessor(context.myProject, context.editor, context.hole).execute(it.value)
        }
    }
}