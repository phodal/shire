package com.phodal.shirecore.provider.codemodel.model

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.phodal.shirecore.provider.codemodel.ClassStructureProvider
import com.phodal.shirecore.provider.codemodel.base.FormatableElement
import com.phodal.shirecore.project.isInProject

class MethodStructure(
    override val root: PsiElement,
    override val text: String,
    override val name: String?,
    val signature: String? = null,
    private val enclosingClass: PsiElement? = null,
    val language: String? = null,
    val returnType: String? = null,
    val paramNames: List<String> = emptyList(),
    private val includeClassContext: Boolean = false,
    private val usages: List<PsiReference> = emptyList(),
    private val fanInOut: List<PsiElement> = emptyList(),
) : FormatableElement(root, text, name) {
    private val classContext: ClassStructure? = if (includeClassContext && enclosingClass != null) {
        ClassStructureProvider.from(enclosingClass, false)
    } else {
        null
    }

    override fun format(): String {
        val usageString = usages.joinToString("\n") {
            val classFile = it.element.containingFile
            val useText = it.element.text
            "${classFile.name} -> $useText"
        }

        var query = """
            path: ${root.containingFile?.virtualFile?.path ?: "_"}
            language: ${language ?: "_"}
            fun name: ${name ?: "_"}
            fun signature: ${signature ?: "_"}
            """.trimIndent()

        if (usageString.isNotEmpty()) {
            query += "\nusages: \n$usageString"
        }

        if (classContext != null) {
            query += classContext.format()
        }

        return query
    }

    fun inputOutputString(): String {
        if (fanInOut.isEmpty()) return ""

        var result = ""
        this.fanInOut.forEach {
            val context: ClassStructure = ClassStructureProvider.from(it, false) ?: return@forEach
            val element = context.root

            if (!isInProject(element.containingFile?.virtualFile!!, root.project)) {
                return@forEach
            }

            context.let { classContext ->
                result += "${classContext.format()}\n"
            }
        }

        if (result.isEmpty()) {
            return ""
        }

        return """
            ```uml
            $result
            ```
            """.trimIndent()
    }
}
