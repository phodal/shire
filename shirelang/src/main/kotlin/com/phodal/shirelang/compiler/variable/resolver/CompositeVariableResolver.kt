package com.phodal.shirelang.compiler.variable.resolver

import com.intellij.psi.PsiElement
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirelang.compiler.variable.base.VariableResolver
import com.phodal.shirelang.compiler.variable.base.VariableResolverContext

class CompositeVariableResolver(
   private val context: VariableResolverContext
) : VariableResolver {
    init {
        val element: PsiElement? = SelectElementStrategy.resolvePsiElement(context.myProject, context.editor)
        context.element = element
    }

    override fun resolve(): Map<String, Any> {
        val resolverList = listOf(
            /**
             * Include ToolchainVariableProvider and PsiContextVariableProvider
             */
            PsiContextVariableResolver(context),
            ToolchainVariableResolver(context),
            ContextVariableResolver(context),
            SystemInfoVariableResolver(context),
            UserCustomVariableResolver(context),
        )

        return resolverList.fold(mutableMapOf()) { acc, resolver ->
            acc.putAll(resolver.resolve())
            acc
        }
    }
}

