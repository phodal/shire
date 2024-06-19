package com.phodal.shirelang.compiler.variable

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiUtilBase
import com.phodal.shirelang.compiler.SymbolTable
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.completion.dataprovider.ContextVariable
import com.phodal.shirelang.run.flow.ShireProcessProcessor.Companion.getElementAtOffset

class CompositeVariableSolver(
    private val myProject: Project,
    private val editor: Editor,
    private val hole: HobbitHole?,
    private val symbolTable: SymbolTable,
) : VariableResolver {

    override fun resolve(): Map<String, Any> {
        val element: PsiElement? = resolvePsiElement()

        // TODO: refactor code
        val results = mutableMapOf<String, Any>()
        results.putAll(BuiltinVariableResolver(myProject, editor, symbolTable, element).resolve())
        results.putAll(ContextVariable.resolve(editor, element))
        results.putAll(SystemInfoVariableResolver().resolve())
        results.putAll(UserCustomVariableResolver(myProject, editor, hole).resolve())
        return results
    }

    private fun resolvePsiElement(): PsiElement? {
        val element: PsiElement? = try {
            editor.caretModel.currentCaret.offset.let {
                val psiFile = PsiUtilBase.getPsiFileInEditor(editor, myProject) ?: return@let null
                getElementAtOffset(psiFile, it)
            }
        } catch (e: Exception) {
            logger<CompositeVariableSolver>().error("Failed to resolve element", e)
            null
        }
        return element
    }
}

