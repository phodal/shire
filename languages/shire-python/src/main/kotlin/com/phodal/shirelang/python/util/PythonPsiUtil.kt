package com.phodal.shirelang.python.util

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
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
        // Replace assigned values of instance attributes with ellipsis
        classCopy.instanceAttributes.forEach {
            (it as PyTargetExpression).findAssignedValue()?.let { assignedValue ->
                assignedValue.replace(makeEllipsisExpression(classCopy.project))
            }
        }

        // Replace assigned values of class attributes with ellipsis
        classCopy.classAttributes.forEach {
            (it as PyTargetExpression).findAssignedValue()?.let { assignedValue ->
                assignedValue.replace(makeEllipsisExpression(classCopy.project))
            }
        }

        // Clear statements in methods
        classCopy.methods.forEach { method ->
            method.statementList.statements.forEach { statement ->
                statement.delete()
            }
        }

        // Recursively clear nested classes
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

        val parameters = function.parameterList.parameters

        val parameterTypes = parameters
            .filterIsInstance<PyTypedElement>()
            .map { context.getType(it) }
            .toMutableList()

        return parameterTypes + resultType
    }
}