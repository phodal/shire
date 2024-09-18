package com.phodal.shirelang.python.util

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiUtilBase
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFunction

object PyTestUtil {
    fun getElementForTests(project: Project, editor: Editor): PsiElement? {
        val element = PsiUtilBase.getElementAtCaret(editor) ?: return null
        val containingFile: PsiFile = element.containingFile ?: return null

        if (InjectedLanguageManager.getInstance(project).isInjectedFragment(containingFile)) {
            return containingFile
        }

        return PsiTreeUtil.getParentOfType(element, PyFunction::class.java, false)
            ?: PsiTreeUtil.getParentOfType(element, PyClass::class.java, false)
            ?: containingFile
    }

    fun getTestNameExample(file: VirtualFile): String {
        val children = file.children
        for (child in children) {
            val fileName = (child ?: continue).name
            if (fileName.endsWith(".py") && !fileName.startsWith("_")) {
                return fileName
            }
        }

        return "test_example.py"
    }

    fun getTestsDirectory(file: VirtualFile, project: Project): VirtualFile {
        val baseDirectory: VirtualFile? = ProjectFileIndex.getInstance(project).getContentRootForFile(file)
        if (baseDirectory == null) {
            val parent = file.parent
            return parent
        }

        val testDir = VfsUtil.createDirectoryIfMissing("tests") ?: baseDirectory
        return testDir
    }

    fun toTestFileName(testFileName: String, exampleName: String): String {
        if (exampleName.startsWith("test_")) return "test_$testFileName.py"
        return "${testFileName}_test.py"
    }
}
