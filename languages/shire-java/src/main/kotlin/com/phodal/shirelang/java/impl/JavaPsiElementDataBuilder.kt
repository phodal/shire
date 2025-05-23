package com.phodal.shirelang.java.impl

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.*
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.search.GlobalSearchScope
import com.phodal.shirecore.provider.codemodel.ClassStructureProvider
import com.phodal.shirecore.provider.codemodel.model.ClassStructure
import com.phodal.shirecore.provider.psi.PsiElementDataBuilder
import com.phodal.shirelang.java.util.JavaContextCollection

open class JavaPsiElementDataBuilder : PsiElementDataBuilder {
    /**
     * Returns the base route of a given Kotlin language method.
     *
     * This method takes a PsiElement as input and checks if it is an instance of PsiMethod. If it is not, an empty string is returned.
     * If the input element is a PsiMethod, the method checks if its containing class has the annotation "@RequestMapping" from the Spring Framework.
     * If the annotation is found, the method retrieves the value attribute of the annotation and returns it as a string.
     * If the value attribute is not a PsiLiteralExpression, an empty string is returned.
     *
     * @param element the PsiElement representing the Kotlin language method
     * @return the base route of the method as a string, or an empty string if the method does not have a base route or if the input element is not a PsiMethod
     */
    override fun baseRoute(element: PsiElement): String {
        if (element !is PsiMethod) return ""

        val containingClass = element.containingClass ?: return ""
        val requestMappingAnnotation = containingClass.annotations.firstOrNull {
            it.qualifiedName?.endsWith("RequestMapping") == true
        } ?: return ""

        val value = requestMappingAnnotation.findAttributeValue("value") as? PsiLiteralExpression
        return value?.value as? String ?: ""
    }

    override fun inboundData(element: PsiElement): Map<String, String> {
        if (element !is PsiMethod) return emptyMap()

        val result = mutableMapOf<String, String>()
        val parameters = element.parameterList.parameters
        for (parameter in parameters) {
            result += handleFromType(parameter)
        }
        return result
    }

    private fun handleFromType(parameter: PsiParameter): Map<@NlsSafe String, String> {
        when (val type = parameter.type) {
            is PsiClassType -> processingClassType(type)
        }

        return emptyMap()
    }

    private fun processing(returnType: PsiType): Map<@NlsSafe String, String> {
        when {
            returnType is PsiClassType -> {
                return processingClassType(returnType)
            }
        }

        return mapOf()
    }

    private fun processingClassType(type: PsiClassType): Map<@NlsSafe String, String> {
        val result = mutableMapOf<String, String>()
        when (type) {
            is PsiClassReferenceType -> {
                type.reference.typeParameters.forEach {
                    result += processing(it)
                }
            }
        }

        type.resolve()?.let {
            val qualifiedName = it.qualifiedName!!
            JavaContextCollection.dataStructure(it)?.let { simpleClassStructure ->
                result += mapOf(qualifiedName to simpleClassStructure.toString())
            }
        }

        return result
    }

    override fun outboundData(element: PsiElement): Map<String, String> {
        if (element !is PsiMethod) return emptyMap()

        val result = mutableMapOf<String, String>()
        val returnType = element.returnType ?: return emptyMap()

        result += processing(returnType)

        return result
    }

    override fun lookupElement(project: Project, canonicalName: String): ClassStructure? {
        val psiFacade = JavaPsiFacade.getInstance(project)

        val psiClass: PsiClass = psiFacade.findClass(canonicalName, GlobalSearchScope.projectScope(project))
                ?: return null

        return ClassStructureProvider.from(psiClass, false)
    }

    override fun parseComment(project: Project, code: String): String? {
        val psiElementFactory = JavaPsiFacade.getInstance(project).elementFactory

        try {
            val docComment = psiElementFactory.createDocCommentFromText(code)

            return docComment.text
        } catch (e: Exception) {
            logger<JavaPsiElementDataBuilder>().warn("Failed to parse comment: $code", e)
        }
        return code
    }
}