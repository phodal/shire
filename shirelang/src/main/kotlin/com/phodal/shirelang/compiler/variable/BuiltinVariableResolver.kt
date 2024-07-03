package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiManager
import com.phodal.shirecore.provider.variable.*
import com.phodal.shirecore.provider.variable.impl.DefaultPsiContextVariableProvider
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirecore.provider.variable.model.VcsToolchainVariable
import com.phodal.shirelang.compiler.variable._base.VariableResolver
import com.phodal.shirelang.compiler.variable._base.VariableResolverContext

class BuiltinVariableResolver(
    private val context: VariableResolverContext,
) : VariableResolver {
    private val variableProvider: PsiContextVariableProvider

    init {
        val psiFile = PsiManager.getInstance(context.myProject).findFile(context.editor.virtualFile)
        variableProvider = if (psiFile?.language != null) {
            PsiContextVariableProvider.provide(psiFile.language)
        } else {
            DefaultPsiContextVariableProvider()
        }
    }

    override fun resolve(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        context.symbolTable.getAllVariables().forEach {
            val psiContextVariable = PsiContextVariable.from(it.key)
            if (psiContextVariable != null) {
                result[it.key] = try {
                    variableProvider.resolve(psiContextVariable, context.myProject, context.editor, context.element)
                } catch (e: Exception) {
                    logger<CompositeVariableResolver>().error("Failed to resolve variable: ${it.key}", e)
                    ""
                }

                return@forEach
            }

            val vcsToolchainVariable = VcsToolchainVariable.from(it.key)
            if (vcsToolchainVariable != null) {
                val provider = ToolchainVariableProvider.provide(vcsToolchainVariable, context.element)
                if (provider != null) {
                    result[it.key] = try {
                        provider.resolve(vcsToolchainVariable, context.myProject, context.editor, context.element)
                    } catch (e: Exception) {
                        logger<CompositeVariableResolver>().error("Failed to resolve variable: ${it.key}", e)
                        ""
                    }

                    return@forEach
                }
            }

            result[it.key] = ""
        }

        return result
    }
}