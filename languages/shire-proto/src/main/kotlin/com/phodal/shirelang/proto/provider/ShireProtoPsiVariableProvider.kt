package com.phodal.shirelang.proto.provider

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.protobuf.lang.PbLanguage
import com.intellij.protobuf.lang.psi.*
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.psi.CodeSmellCollector
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirelang.proto.codemodel.ProtoClassStructureProvider
import com.phodal.shirelang.proto.codemodel.ProtoFileStructureProvider

class ShireProtoPsiVariableProvider : PsiContextVariableProvider {
    override fun resolve(variable: PsiContextVariable, project: Project, editor: Editor, psiElement: PsiElement?): Any {
        if (psiElement?.language !is PbLanguage) return ""

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
            PsiContextVariable.RELATED_CLASSES -> {
                if (psiElement !is PbServiceDefinition) return ""
                ShireProtoUtils.lookupDependenceMessages(psiElement, project).map {
                    it.text ?: ""
                }
            }
            PsiContextVariable.IMPORTS -> {
                return containingFile.importStatements.joinToString("\n") { it.text }
            }
            PsiContextVariable.FRAMEWORK_CONTEXT -> return collectFrameworkContext(psiElement, project)
            PsiContextVariable.CODE_SMELL -> return CodeSmellCollector.collectElementProblemAsSting(
                psiElement,
                project,
                editor
            )
            PsiContextVariable.METHOD_CALLER -> ShireProtoUtils.lookupUsage(psiElement, project).joinToString("\n")
            PsiContextVariable.CALLED_METHOD -> {
                if (psiElement !is PbServiceDefinition) return ""
                ShireProtoUtils.lookupDependenceMessages(psiElement, project).map {
                    it.text ?: ""
                }
            }
            PsiContextVariable.STRUCTURE -> when (psiElement) {
                is PbFile -> ProtoFileStructureProvider().build(psiElement)?.toString() ?: ""
                is PbMessageDefinition,
                is PbServiceDefinition,
                    -> ProtoClassStructureProvider().build(psiElement, true)?.toString() ?: ""

                else -> null
            } ?: ""

            PsiContextVariable.SIMILAR_CODE -> ""
            PsiContextVariable.SIMILAR_TEST_CASE -> ""
            PsiContextVariable.IS_NEED_CREATE_FILE -> ""
            PsiContextVariable.TARGET_TEST_FILE_NAME -> ""
            PsiContextVariable.UNDER_TEST_METHOD_CODE -> ""
            PsiContextVariable.CHANGE_COUNT -> calculateChangeCount(psiElement)
            PsiContextVariable.LINE_COUNT -> calculateLineCount(psiElement)
            PsiContextVariable.COMPLEXITY_COUNT -> calculateComplexityCount(psiElement)
        }
    }
}
