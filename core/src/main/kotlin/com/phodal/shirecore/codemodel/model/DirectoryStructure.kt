package com.phodal.shirecore.codemodel.model

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.codemodel.base.FormatableElement

class DirectoryStructure(
    override val root: PsiFile,
    override val name: String,
    private val path: String,
    /**
     * For some languages, path may not be enough to uniquely identify a package, like in Java, Kotlin, etc.
     */
    private val packageString: String? = null,
    private val files: List<FileStructure> = emptyList(),
) : FormatableElement(root, path, name) {
    /**
     * This method is used to format the details of the directory, package, and files into a string.
     * The formatted string includes the directory name, package name (if available), and the details of each file.
     * Each file's details include the file name and the names of the classes within the file.
     *
     * The method first creates a string representation of the file details. For each file, it appends the file name and the names of the classes within the file.
     * Then, it constructs the final string by appending the directory name, package name (if available), and the file details.
     *
     * @return A string representation of the directory, package, and file details. The string is formatted as follows:
     * ```
     * directory name: /path/to/file
     * package: com.example
     * file `file1` classes: [class1, class2]
     * file `file2` classes: [class3, class4]
     * ```
     * If the package name is not available, it is omitted from the string. If there are no file details, they are also omitted from the string.
     */
    override fun format(): String {
        val fileDetails = files.joinToString("\n") { structure ->
            val file = structure.root
            val classes = structure.classes.mapNotNull {
                when (it) {
                    is PsiNameIdentifierOwner -> it.name
                    else -> null
                }
            }.joinToString(", ")
            "file `${file.name}` classes: [$classes]"
        }

        val filePath = path
        val filePackage = if (packageString != null) "package: $packageString" else ""

        return buildString {
            append("directory name: $filePath\n")
            if (filePackage.isNotEmpty()) append("$filePackage\n")
            if (fileDetails.isNotEmpty()) append("$fileDetails\n")
        }
    }
}