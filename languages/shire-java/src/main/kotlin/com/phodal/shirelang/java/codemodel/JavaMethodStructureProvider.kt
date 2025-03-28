package com.phodal.shirelang.java.codemodel

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiNameIdentifierOwner
import com.phodal.shirecore.provider.codemodel.MethodStructureProvider
import com.phodal.shirecore.provider.codemodel.model.MethodStructure
import com.phodal.shirelang.java.util.JavaTypeResolver
import java.util.concurrent.Future

class JavaMethodStructureProvider : MethodStructureProvider {
    override fun build(psiElement: PsiElement, includeClassContext: Boolean, gatherUsages: Boolean): MethodStructure? {
        if (psiElement !is PsiMethod) {
            return null
        }

        val parameterList = runReadAction { psiElement.parameters.mapNotNull { it.name } }
        val variableContextList = parameterList.map { it }

        val usagesList = if (gatherUsages) {
            JavaClassStructureProvider.findUsages(psiElement as PsiNameIdentifierOwner)
        } else {
            emptyList()
        }

        val ios: List<PsiElement> = try {
            val executeOnPooledThread: Future<List<PsiElement>> =
                ApplicationManager.getApplication().executeOnPooledThread<List<PsiElement>> {
                    return@executeOnPooledThread JavaTypeResolver.resolveByMethod(psiElement).values.map<PsiClass, PsiClass> { it }
                }

            executeOnPooledThread.get()
        } catch (e: Exception) {
            emptyList()
        }

        return ApplicationManager.getApplication().executeOnPooledThread<MethodStructure> {
            runReadAction {
                MethodStructure(
                    psiElement,
                    text = psiElement.text,
                    name = psiElement.name,
                    signature = getSignatureString(psiElement),
                    enclosingClass = psiElement.containingClass,
                    language = psiElement.language.displayName,
                    returnType = processReturnTypeText(psiElement.returnType?.presentableText),
                    variableContextList,
                    includeClassContext,
                    usagesList,
                    ios
                )
            }
        }.get()
    }

    private fun processReturnTypeText(returnType: String?): String? {
        return if (returnType == "void") null else returnType
    }

    companion object {
        fun getSignatureString(method: PsiMethod): String {
            val bodyStart = runReadAction { method.body?.startOffsetInParent ?: method.textLength }
            val text = runReadAction { method.text }
            val substring = text.substring(0, bodyStart)
            val trimmed = substring.replace('\n', ' ').trim()
            return trimmed
        }
    }
}
