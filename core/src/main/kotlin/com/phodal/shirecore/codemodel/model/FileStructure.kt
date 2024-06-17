package com.phodal.shirecore.codemodel.model

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.codemodel.ClassStructureProvider
import com.phodal.shirecore.codemodel.base.FormatableElement

class FileStructure(
    override val root: PsiFile,
    override val name: String,
    private val path: String,
    private val packageString: String? = null,
    private val imports: List<PsiElement> = emptyList(),
    val classes: List<PsiNameIdentifierOwner> = emptyList(),
    private val methods: List<PsiNameIdentifierOwner> = emptyList(),
) : FormatableElement(root, path, name) {
    private fun getClassDetail(): List<String> = classes.mapNotNull {
        ClassStructureProvider.from(it, false)?.format()
    }

    override fun format(): String {
        fun getFieldString(fieldName: String, fieldValue: String): String {
            return if (fieldValue.isNotBlank()) "$fieldName: $fieldValue" else ""
        }

        val filePackage = getFieldString("file package", packageString ?: "")
        val fileImports = getFieldString(
            "file imports",
            if (imports.isNotEmpty()) imports.joinToString(" ", transform = { it.text }) else ""
        )
        val classDetails =
            getFieldString(
                "file classes",
                if (getClassDetail().isNotEmpty()) getClassDetail().joinToString(", ") else ""
            )
        val filePath = getFieldString("file path", path)

        return buildString {
            append("file name: $name\n")
            if (filePackage.isNotEmpty()) append("$filePackage\n")
            if (fileImports.isNotEmpty()) append("$fileImports\n")
            if (classDetails.isNotEmpty()) append("$classDetails\n")
            append("$filePath\n")
        }
    }
}
