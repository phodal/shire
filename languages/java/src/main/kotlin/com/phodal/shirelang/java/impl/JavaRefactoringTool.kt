package com.phodal.shirelang.java.impl

import com.intellij.codeInsight.daemon.impl.quickfix.RenameElementFix
import com.intellij.codeInsight.daemon.impl.quickfix.SafeDeleteFix
import com.intellij.codeInspection.MoveToPackageFix
import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.phodal.shirecore.provider.RefactoringTool
import com.phodal.shirecore.refactoring.RefactorInstElement

class JavaRefactoringTool : RefactoringTool {
    private val project = ProjectManager.getInstance().openProjects.firstOrNull()

    override fun lookupFile(path: String): PsiFile? {
        if (project == null) return null

        val elementInfo = getElementInfo(path, null) ?: return null
        val searchScope = ProjectScope.getProjectScope(project)
        val javaFiles: List<PsiJavaFile> = FileTypeIndex.getFiles(JavaFileType.INSTANCE, searchScope)
            .mapNotNull { PsiManager.getInstance(project).findFile(it) as? PsiJavaFile }

        val className = elementInfo.className
        val packageName = elementInfo.pkgName

        val sourceFile = javaFiles.firstOrNull {
            it.packageName == packageName && it.name == "$className.java"
        } ?: return null

        return sourceFile
    }

    override fun rename(sourceName: String, targetName: String, psiFile: PsiFile?): Boolean {
        if (project == null) return false
        val elementInfo = getElementInfo(sourceName, psiFile) ?: return false

        val element: PsiNamedElement =
            if (psiFile != null) {
                if (psiFile is PsiJavaFile) {
                    val methodName = elementInfo.methodName
                    val className = elementInfo.className

                    val psiMethod: PsiMethod? =
                        psiFile.classes.firstOrNull { it.name == className }
                            ?.methods?.firstOrNull { it.name == methodName }

                    psiMethod ?: psiFile
                } else {
                    psiFile
                }

            } else {
                searchPsiElementByName(elementInfo, sourceName) ?: return false
            }

        try {
            RenameElementFix(element, targetName)
                .invoke(project, element.containingFile, element, element)

            performRefactoringRename(project, element, targetName)
        } catch (e: Exception) {
            return false
        }

        return true
    }

    /**
     * Deletes the given PsiElement in a safe manner, ensuring that no syntax errors or unexpected behavior occur as a result.
     * The method performs checks before deletion to confirm that it is safe to remove the element from the code structure.
     *
     * @param element The PsiElement to be deleted. This should be a valid element within the PSI tree structure.
     * @return true if the element was successfully deleted without any issues, false otherwise. This indicates whether
     * the deletion was performed and considered safe.
     */
    override fun safeDelete(element: PsiElement): Boolean {
        val delete = SafeDeleteFix(element)
        try {
            delete.invoke(element.project, element.containingFile, element, element)
        } catch (e: Exception) {
            return false
        }

        return true
    }

    /**
     * In Java the canonicalName is the fully qualified name of the target package.
     * In Kotlin the canonicalName is the fully qualified name of the target package or class.
     */
    override fun move(element: PsiElement, canonicalName: String): Boolean {
        val file = element.containingFile
        val fix = MoveToPackageFix(file, canonicalName)

        try {
            fix.invoke(file.project, file, element, element)
        } catch (e: Exception) {
            return false
        }

        return true
    }

    private fun searchPsiElementByName(refactorInstElement: RefactorInstElement, sourceName: String): PsiNamedElement? = runReadAction {
        when {
            refactorInstElement.isMethod -> {
                val className = refactorInstElement.className
                val javaFile = this.lookupFile(sourceName) as? PsiJavaFile ?: return@runReadAction null

                val psiMethod: PsiMethod =
                    javaFile.classes.firstOrNull { it.name == className }
                        ?.methods?.firstOrNull { it.name == refactorInstElement.methodName }
                        ?: return@runReadAction null

                psiMethod
            }

            refactorInstElement.isClass -> {
                val javaFile = this.lookupFile(sourceName) as? PsiJavaFile ?: return@runReadAction null
                javaFile.classes.firstOrNull { it.name == refactorInstElement.className }
            }

            else -> {
                val javaFile = this.lookupFile(sourceName) as? PsiJavaFile ?: return@runReadAction null
                javaFile
            }
        }
    }

    /**
     * input will be canonicalName#methodName or just methodName
     */
    private fun getElementInfo(input: String, psiFile: PsiFile?): RefactorInstElement? {
        if (!input.contains("#") && psiFile != null) {
            val javaFile = psiFile as? PsiJavaFile ?: return null
            val className = javaFile.classes.firstOrNull()?.name ?: return null
            val canonicalName = javaFile.packageName + "." + className
            return RefactorInstElement(true, true, input, canonicalName, className, javaFile.packageName)
        }

        val isMethod = input.contains("#")
        val methodName = input.substringAfter("#")
        val canonicalName = input.substringBefore("#")
        val maybeClassName = canonicalName.substringAfterLast(".")
        // the clasName should be Uppercase, or it will be the package
        var isClass = false
        var pkgName = canonicalName.substringBeforeLast(".")
        if (maybeClassName[0].isLowerCase()) {
            pkgName = "$pkgName.$maybeClassName"
        } else {
            isClass = true
        }

        return RefactorInstElement(isClass, isMethod, methodName, canonicalName, maybeClassName, pkgName)
    }
}
