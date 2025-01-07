package com.phodal.shirelang.compiler.variable.resolver

import com.phodal.shirelang.compiler.execute.PatternActionProcessor
import com.phodal.shirelang.compiler.variable.resolver.base.VariableResolver
import com.phodal.shirelang.compiler.variable.resolver.base.VariableResolverContext
import com.phodal.shirelang.debugger.snapshot.VariableSnapshotRecorder

class UserCustomVariableResolver(
    private val context: VariableResolverContext,
) : VariableResolver {
    private val record = VariableSnapshotRecorder.getInstance(context.myProject)
    override suspend fun resolve(initVariables: Map<String, Any>): Map<String, String> {
        record.clear()

        val vars: MutableMap<String, Any?> = initVariables.toMutableMap()
        return context.hole?.variables?.mapValues {
            PatternActionProcessor(context.myProject, context.hole, vars).execute(it.value)
        } ?: emptyMap()
    }
}