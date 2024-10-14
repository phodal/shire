package com.phodal.shirelang.compiler.variable.resolver

import com.intellij.openapi.application.ReadAction
import com.intellij.psi.PsiElement
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirelang.compiler.variable.resolver.base.VariableResolver
import com.phodal.shirelang.compiler.variable.resolver.base.VariableResolverContext

class CompositeVariableResolver(
   private val context: VariableResolverContext
) : VariableResolver {
    init {
        context.element = ReadAction.compute<PsiElement?, Throwable> {
            SelectElementStrategy.resolvePsiElement(context.myProject, context.editor)
        }
    }

    override suspend fun resolve(initVariables: Map<String, Any>): Map<String, Any> {
        val resolverList = listOf(
            PsiContextVariableResolver(context),
            ToolchainVariableResolver(context),
            ContextVariableResolver(context),
            SystemInfoVariableResolver(context),
            UserCustomVariableResolver(context),
        )

        val initial = initVariables.toMutableMap()
        return resolverList.fold(initial) { acc: MutableMap<String, Any>, resolver: VariableResolver ->
            acc.putAll(resolver.resolve(acc))
            acc
        }
    }
}

