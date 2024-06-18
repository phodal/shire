package com.phodal.shirecore.codemodel.model

import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.phodal.shirecore.codemodel.MethodStructureProvider
import com.phodal.shirecore.codemodel.VariableStructureProvider
import com.phodal.shirecore.codemodel.base.FormatableElement

class ClassStructure(
    override val root: PsiElement,
    override val text: String?,
    override val name: String?,
    val displayName: String?,
    val methods: List<PsiElement> = emptyList(),
    val fields: List<PsiElement> = emptyList(),
    val superClasses: List<String>? = null,
    val annotations: List<String> = mutableListOf(),
    val usages: List<PsiReference> = emptyList(),
) : FormatableElement(root, text, name) {
    private fun getFieldNames(): List<String> = fields.map {
        VariableStructureProvider.from(it,
            includeMethodContext = false,
            includeClassContext = false,
            gatherUsages = false
        ).shortFormat()
    }

    private fun getMethodSignatures(): List<String> = methods.mapNotNull {
        MethodStructureProvider.from(it, false, gatherUsages = false)?.signature
    }

    override fun format(): String {
        val className = name ?: "_"
        val classFields = getFieldNames().joinToString(separator = "\n  ")
        val superClasses = when {
            superClasses.isNullOrEmpty() -> ""
            else -> " : ${superClasses.joinToString(separator = ", ")}"
        }
        val methodSignatures = getMethodSignatures()
            .filter { it.isNotBlank() }
            .joinToString(separator = "\n  ") { method ->
                "+ $method"
            }

        val filePath = displayName ?: runReadAction { root.containingFile?.virtualFile?.path }
        val annotations = if (annotations.isEmpty()) {
            ""
        } else {
            "\n'" + annotations.joinToString(separator = ", ")
        }

        return """
        |'package: $filePath$annotations
        |class $className$superClasses {
        |  $classFields
        |  $methodSignatures
        |}
    """.trimMargin()
    }
}