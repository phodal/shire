package com.phodal.shirelang.compiler.variable

import com.phodal.shirelang.compiler.patternaction.PatternActionProcessor

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