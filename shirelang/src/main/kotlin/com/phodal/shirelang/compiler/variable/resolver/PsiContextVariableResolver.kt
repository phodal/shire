package com.phodal.shirelang.compiler.variable.resolver

import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.impl.DefaultPsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirelang.compiler.variable.base.VariableResolver
import com.phodal.shirelang.compiler.variable.base.VariableResolverContext

/**
 * Include ToolchainVariableProvider and PsiContextVariableProvider
 */
class PsiContextVariableResolver(
    private val context: VariableResolverContext,
) : VariableResolver {
    private val variableProvider: PsiContextVariableProvider

    init {
        val psiFile = ReadAction.compute<PsiFile?, Throwable> {
            PsiManager.getInstance(context.myProject).findFile(context.editor.virtualFile)
        }

        variableProvider = if (psiFile?.language != null) {
            PsiContextVariableProvider.provide(psiFile.language)
        } else {
            DefaultPsiContextVariableProvider()
        }
    }

    override suspend fun resolve(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        context.variableTable.getAllVariables().forEach {
            val psiContextVariable = PsiContextVariable.from(it.key)
            if (psiContextVariable != null) {
                result[it.key] = try {
                    ReadAction.compute<Any, Throwable> {
                        variableProvider.resolve(psiContextVariable, context.myProject, context.editor, context.element)
                    }
                } catch (e: Exception) {
                    logger<CompositeVariableResolver>().error("Failed to resolve variable: ${it.key}", e)
                    ""
                }

                return@forEach
            }
        }

        return result
    }
}