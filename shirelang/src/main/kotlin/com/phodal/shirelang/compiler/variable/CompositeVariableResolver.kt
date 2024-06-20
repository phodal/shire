package com.phodal.shirelang.compiler.variable

import com.intellij.psi.PsiElement
import com.phodal.shirecore.middleware.select.SelectElementStrategy
import com.phodal.shirelang.compiler.variable._base.VariableResolver
import com.phodal.shirelang.compiler.variable._base.VariableResolverContext

class CompositeVariableResolver(
   private val context: VariableResolverContext
) : VariableResolver {
    init {
        val element: PsiElement? = SelectElementStrategy.resolvePsiElement(context.myProject, context.editor)
        context.element = element
    }

    override fun resolve(): Map<String, Any> {
        val resolverList = listOf(
            BuiltinVariableResolver(context),
            ContextVariableResolver(context),
            SystemInfoVariableResolver(context),
            UserCustomVariableResolver(context)
        )

        return resolverList.fold(mutableMapOf()) { acc, resolver ->
            acc.putAll(resolver.resolve())
            acc
        }
    }
}

