package com.phodal.shirelang.java.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaFile
import com.phodal.shirecore.provider.CodeStructVariable
import com.phodal.shirecore.provider.CodeStructVariableProvider
import com.phodal.shirelang.java.toolchain.getContainingClass

class JavaCodeStructVariableProvider : CodeStructVariableProvider {
    override fun calculateVariable(psiElement: PsiElement, variable: CodeStructVariable): String {
        if (psiElement.language.id != "JAVA") {
            return ""
        }

        val clazz: PsiClass? = psiElement.getContainingClass()
        val javaFile: PsiJavaFile = psiElement.containingFile as PsiJavaFile

        return when (variable) {
            CodeStructVariable.CURRENT_CLASS_NAME -> {
                clazz?.name ?: ""
            }
            CodeStructVariable.CURRENT_CLASS_CODE -> {
                javaFile.text
            }
            CodeStructVariable.RELATED_CLASSES -> {
                ""
            }
            CodeStructVariable.SIMILAR_TEST_CASE -> {
                ""
            }
            CodeStructVariable.IMPORTS -> {
                javaFile.importList?.text ?: ""
            }
            CodeStructVariable.IS_NEW_FILE -> {
                // todo check has target test fiel
                ""
            }
            CodeStructVariable.TARGET_TEST_FILE_NAME -> {
                ""
            }
        }
    }

}
