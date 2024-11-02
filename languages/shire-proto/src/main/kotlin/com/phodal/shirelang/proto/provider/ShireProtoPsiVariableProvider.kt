package com.phodal.shirelang.proto.provider

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.protobuf.lang.ProtoBaseLanguage
import com.intellij.protobuf.lang.psi.PbEnumDefinition
import com.intellij.protobuf.lang.psi.PbFile
import com.intellij.protobuf.lang.psi.PbMessageDefinition
import com.intellij.protobuf.lang.psi.PbServiceDefinition
import com.intellij.protobuf.lang.psi.PbServiceMethod
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.impl.CodeSmellBuilder
import com.phodal.shirecore.provider.variable.model.PsiContextVariable

class ShireProtoPsiVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language !is ProtoBaseLanguage) return ""

        val containingFile: PbFile = psiElement.containingFile as? PbFile ?: return ""

        return when (variable) {
            PsiContextVariable.CURRENT_CLASS_NAME -> {
                return when (psiElement) {
                    is PbFile -> psiElement.name ?: ""
                    is PbMessageDefinition -> psiElement.name ?: ""
                    is PbEnumDefinition -> psiElement.name ?: ""
                    is PbServiceDefinition -> psiElement.name ?: ""
                    else -> ""
                }
            }

            PsiContextVariable.CURRENT_CLASS_CODE -> return psiElement.text
            PsiContextVariable.CURRENT_METHOD_NAME -> {
                return when (psiElement) {
                    is PbServiceMethod -> psiElement.nameIdentifier ?: ""
                    else -> ""
                }
            }
            PsiContextVariable.CURRENT_METHOD_CODE -> return psiElement.text
            PsiContextVariable.RELATED_CLASSES -> TODO()
            PsiContextVariable.SIMILAR_TEST_CASE -> TODO()
            PsiContextVariable.IMPORTS -> {
                return containingFile.importStatements.joinToString("\n") { it.text }
            }
            PsiContextVariable.IS_NEED_CREATE_FILE -> ""
            PsiContextVariable.TARGET_TEST_FILE_NAME -> ""
            PsiContextVariable.UNDER_TEST_METHOD_CODE -> ""
            PsiContextVariable.FRAMEWORK_CONTEXT -> return collectFrameworkContext(psiElement, project)
            PsiContextVariable.CODE_SMELL -> return CodeSmellBuilder.collectElementProblemAsSting(
                psiElement,
                project,
                editor
            )

            PsiContextVariable.METHOD_CALLER -> TODO()
            PsiContextVariable.CALLED_METHOD -> TODO()
            PsiContextVariable.SIMILAR_CODE -> TODO()
            PsiContextVariable.STRUCTURE -> TODO()
        }
    }
}
