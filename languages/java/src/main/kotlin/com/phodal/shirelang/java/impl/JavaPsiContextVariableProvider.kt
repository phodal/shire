package com.phodal.shirelang.java.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaFile
import com.phodal.shirecore.provider.PsiVariable
import com.phodal.shirecore.provider.PsiContextVariableProvider
import com.phodal.shirelang.java.toolchain.getContainingClass

class JavaPsiContextVariableProvider : PsiContextVariableProvider {
    override fun resolveVariableValue(psiElement: PsiElement, variable: PsiVariable): String {
        if (psiElement.language.id != "JAVA") {
            return ""
        }

        val clazz: PsiClass? = psiElement.getContainingClass()
        val javaFile: PsiJavaFile = psiElement.containingFile as PsiJavaFile

        return when (variable) {
            PsiVariable.CURRENT_CLASS_NAME -> {
                clazz?.name ?: ""
            }
            PsiVariable.CURRENT_CLASS_CODE -> {
                javaFile.text
            }
            PsiVariable.RELATED_CLASSES -> {
                ""
            }
            PsiVariable.SIMILAR_TEST_CASE -> {
                ""
            }
            PsiVariable.IMPORTS -> {
                javaFile.importList?.text ?: ""
            }
            PsiVariable.IS_NEW_FILE -> {
                // todo check has target test fiel
                ""
            }
            PsiVariable.TARGET_TEST_FILE_NAME -> {
                ""
            }
        }
    }

}
