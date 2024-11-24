package com.phodal.shirelang.psi

import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.FakePsiElement

class ShireTask(val presentableName: String)

class ShireCommentRunPsi(private val myPsiManager: PsiManager, val task: ShireTask) : FakePsiElement() {
    override fun getParent(): PsiElement? {
        return null
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun getManager(): PsiManager {
        return myPsiManager
    }

    override fun getContainingFile(): PsiFile {
        return PsiFileFactory.getInstance(project).createFileFromText("foo.txt", FileTypes.PLAIN_TEXT, "")
    }

    override fun getName(): String? {
        return task.presentableName
    }
}