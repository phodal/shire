package com.phodal.shirelang.kotlin.impl

import com.intellij.codeInsight.daemon.impl.quickfix.RenameElementFix
import com.intellij.codeInsight.daemon.impl.quickfix.SafeDeleteFix
import com.intellij.codeInspection.MoveToPackageFix
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.*
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.ProjectScope
import com.phodal.shirecore.provider.shire.RefactoringTool
import com.phodal.shirecore.variable.toolchain.refactoring.RefactorInstElement
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction

class KotlinRefactoringTool : RefactoringTool {
    private val project = ProjectManager.getInstance().openProjects.firstOrNull()

    override fun lookupFile(path: String): PsiFile? {
        if (project == null) return null

        val elementInfo = getElementInfo(path, null) ?: return null
        val searchScope = ProjectScope.getProjectScope(project)

        val ktFiles = FileTypeIndex.getFiles(KotlinFileType.INSTANCE, searchScope)
            .mapNotNull { PsiManager.getInstance(project).findFile(it) as? KtFile }

        val className = elementInfo.className
        val packageName = elementInfo.pkgName

        val sourceFile = ktFiles.firstOrNull {
            it.packageFqName.asString() == packageName && it.name == "$className.kt"
        } ?: return null

        return sourceFile
    }

    override fun rename(sourceName: String, targetName: String, psiFile: PsiFile?): Boolean {
        if (project == null) return false
        val retrieveElementInfo = getElementInfo(sourceName, psiFile)
        val elementInfo = if (retrieveElementInfo != null) {
            retrieveElementInfo
        } else {
            return false
        }

        val element: PsiNamedElement =
            if (psiFile != null) {
                if (psiFile is KtFile) {
                    val methodName = elementInfo.methodName
                    val className = elementInfo.className
                    val pkgName = elementInfo.pkgName

                    if (elementInfo.isMethod) {
                        val findClassAndMethod: PsiMethod? = psiFile.classes
                            .firstOrNull {
                                it.name == className
                            }
                            ?.findMethodsByName(methodName, false)?.firstOrNull()

                        // lookup by function only
                        findClassAndMethod ?: (psiFile.declarations
                            .filterIsInstance<KtNamedFunction>()
                            .firstOrNull {
                                it.name == methodName
                            } ?: return false)
                    } else {
                        psiFile.declarations
                            .filterIsInstance<KtFile>()
                            .firstOrNull {
                                it.name == className && it.packageFqName.asString() == pkgName
                            } ?: return false
                    }
                } else {
                    return false
                }
            } else {
                return false
            }

        val rename = RenameElementFix(element, targetName)

        try {
            rename.invoke(project, psiFile, element, element)
        } catch (e: Exception) {
            logger<KotlinRefactoringTool>().error("Error in renaming", e)
            return false
        }

        return true
    }

    private fun getElementInfo(input: String, psiFile: PsiFile?): RefactorInstElement? {
        if (!input.contains("#") && psiFile != null) {
            val kotlinFile = psiFile as? KtFile ?: return null
            val className = kotlinFile.classes.firstOrNull()?.name ?: return null
            val canonicalName = kotlinFile.packageFqName.asString() + "." + className
            return RefactorInstElement(true, true, input, canonicalName, className, kotlinFile.packageFqName.asString())
        }

        val isMethod = input.contains("#")
        val methodName = input.substringAfter("#")
        val canonicalName = input.substringBefore("#")
        val maybeClassName = canonicalName.substringAfterLast(".")
        // the clasName should be Uppercase or it will be the package
        var isClass = false
        var pkgName = canonicalName.substringBeforeLast(".")
        if (maybeClassName[0].isLowerCase()) {
            pkgName = "$pkgName.$maybeClassName"
        } else {
            isClass = true
        }

        return RefactorInstElement(isClass, isMethod, methodName, canonicalName, maybeClassName, pkgName)
    }

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
}
