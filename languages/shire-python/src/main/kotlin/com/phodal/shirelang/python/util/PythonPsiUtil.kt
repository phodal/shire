package com.phodal.shirelang.python.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.concurrency.annotations.RequiresReadLock
import com.jetbrains.python.psi.*
import com.jetbrains.python.psi.types.PyType
import com.jetbrains.python.psi.types.TypeEvalContext

object PythonPsiUtil {
    fun getImportsInFile(file: PsiFile): String {
        if (file !is PyFile) return ""

        val fromImports = file.fromImports
            .map { it.text }.distinct()
            .joinToString("\n")

        val imports = file.importTargets
            .asSequence()
            .map { it.parent.text }
            .distinct()
            .joinToString("\n")

        return (imports + "\n" + fromImports).trimIndent()
    }

    fun getClassWithoutMethod(clazz: PyClass, function: PyFunction): PyClass {
        val classCopy = clazz.copy() as PyClass
        val methods = classCopy.methods

        val methodsToDelete = methods.filter { it.name == function.name }
        methodsToDelete.forEach {
            it.delete()
        }

        return classCopy
    }

    private fun getFunctionSignature(function: PyFunction, inplace: Boolean): PyFunction {
        val functionCopy = if (inplace) {
            function
        } else {
            function.copy() as PyFunction
        }

        functionCopy.statementList.statements.forEach { it.delete() }
        return functionCopy
    }

    fun clearClass(classCopy: PyClass) {
        classCopy.instanceAttributes.forEach {
            it.findAssignedValue()?.replace(makeEllipsisExpression(classCopy.project))
        }

        classCopy.classAttributes.forEach {
            it.findAssignedValue()?.replace(makeEllipsisExpression(classCopy.project))
        }

        classCopy.methods.forEach { method ->
            method.statementList.statements.forEach { statement ->
                statement.delete()
            }
        }

        classCopy.nestedClasses.forEach { nestedClass ->
            clearClass(nestedClass)
        }
    }

    private fun makeEllipsisExpression(project: Project): PyExpression {
        return PyElementGenerator.getInstance(project).createEllipsis()
    }

    @RequiresReadLock
    fun findRelatedTypes(function: PyFunction): List<PyType?> {
        val context = TypeEvalContext.codeCompletion(function.project, function.containingFile)

        val resultType = function.getReturnStatementType(context)

        val parameters = (function as? PyCallable)?.parameterList?.parameters?.toList() ?: emptyList()

        val parameterTypes = parameters
            .filterIsInstance<PyTypedElement>()
            .map { context.getType(it) }
            .toMutableList()

        return parameterTypes + resultType
    }

    fun collectAndResolveReferences(psiElement: PsiElement): String {
        val list = mutableListOf<String>()
        psiElement.accept(object : PyRecursiveElementVisitor() {
            override fun visitPyCallExpression(expression: PyCallExpression) {
                super.visitPyCallExpression(expression)
                val callee = expression.callee
                val resolved = callee?.reference?.resolve()
                addResolvedElement(expression.text, resolved)
            }

            override fun visitPyReferenceExpression(expression: PyReferenceExpression) {
                super.visitPyReferenceExpression(expression)
                val resolved = expression.reference.multiResolve(false).firstOrNull()?.element
                addResolvedElement(expression.text, resolved)
            }

            private fun addResolvedElement(declarationName: String, element: PsiElement?) {
                if (element == null || ProjectFileIndex.getInstance(element.project)
                        .isInLibrary(element.containingFile.virtualFile)
                ) return

                val resolvedElement = if (PyUtil.isInitOrNewMethod(element)) {
                    PsiTreeUtil.getParentOfType(element, PyClass::class.java, false)
                } else {
                    element
                }

                when (resolvedElement) {
                    is PyFunction -> addFunction(declarationName, resolvedElement)
                    is PyClass -> addClass(declarationName, resolvedElement)
                }
            }

            private fun addClass(declarationName: String, clazz: PyClass) {
                list.add(clazz.text)
            }

            private fun addFunction(declarationName: String, function: PyFunction) {
                list.add(function.text)
            }
        })

        return list.joinToString("\n")
    }
}