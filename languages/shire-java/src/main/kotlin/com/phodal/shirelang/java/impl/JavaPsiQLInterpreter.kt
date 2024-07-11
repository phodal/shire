package com.phodal.shirelang.java.impl

import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.phodal.shirecore.provider.variable.PsiQLInterpreter


enum class JavaPsiQLInterpreterMethod(val methodName: String, val description: String) {
    GET_NAME("getName", "Get class name"),
    NAME("name", "Get class name"),
    EXTENDS("extends", "Get class extends"),
    IMPLEMENTS("implements", "Get class implements"),
    METHOD_CODE_BY_NAME("methodCodeByName", "Get method code by name"),
    FIELD_CODE_BY_NAME("fieldCodeByName", "Get field code by name"),

    SUBCLASSES_OF("subclassesOf", "Get subclasses of class"),
    ANNOTATED_OF("annotatedOf", "Get annotated classes"),
    SUPERCLASS_OF("superclassOf", "Get superclass of class"),
    IMPLEMENTS_OF("implementsOf", "Get implemented interfaces of class"),
}

class JavaPsiQLInterpreter : PsiQLInterpreter {
    override fun supportsMethod(language: Language, methodName: String): List<String> {
        if (language.id != "JAVA") {
            return emptyList()
        }

        return JavaPsiQLInterpreterMethod.values().map { it.methodName }
    }

    /**
     * clazz.getName() or clazz.extensions
     */
    override fun resolveCall(element: PsiElement, methodName: String, arguments: List<Any>): Any {
        // is of method
        if (methodName.endsWith("Of")) {
            return this.resolveOfTypedCall(element.project, methodName, arguments)
        }

        return when (element) {
            is PsiClass -> {
                when (methodName) {
                    "getName" -> element.name!!
                    "name" -> element.name!!
                    "extends" -> element.extendsList?.referencedTypes?.map { it.name } ?: emptyList<String>()
                    "implements" -> element.implementsList?.referencedTypes?.map { it.name } ?: emptyList<String>()
                    "methodCodeByName" -> {
                        val method = element.methods.firstOrNull { it.name == arguments.first() } ?: return ""
                        method.body?.text ?: ""
                    }

                    "fieldCodeByName" -> {
                        val field = element.fields.firstOrNull { it.name == arguments.first() } ?: return ""
                        field.text
                    }

                    else -> ""
                }
            }

            else -> ""
        }
    }

    override fun resolveOfTypedCall(project: Project, methodName: String, arguments: List<Any>): Any {
        // get first argument for infer type
        val firstArgument = arguments.firstOrNull().toString()
        if (firstArgument.isBlank()) {
            logger<JavaPsiQLInterpreter>().warn("Cannot find first argument")
            return ""
        }

        return when (methodName) {
            "subclassesOf" -> {
                val facade = JavaPsiFacade.getInstance(project)

                val psiClass = facade.findClass(firstArgument, GlobalSearchScope.allScope(project))
                if (psiClass == null) {
                    logger<JavaPsiQLInterpreter>().warn("Cannot find class: $firstArgument")
                    return ""
                }

                val map: List<PsiClass> =
                    ClassInheritorsSearch.search(psiClass, GlobalSearchScope.allScope(project), true).map { it }
                map
            }

            "annotatedOf" -> {
                val facade = JavaPsiFacade.getInstance(project)
                val annotationClass =
                    facade.findClass(firstArgument, GlobalSearchScope.allScope(project)) ?: return emptyList<String>()

                val annotatedElements =
                    AnnotatedElementsSearch.searchPsiClasses(annotationClass, GlobalSearchScope.allScope(project))
                        .findAll()

                annotatedElements.filterIsInstance<PsiClass>()
            }

            "superclassOf" -> {
                val psiClass = searchClass(project, firstArgument) ?: return ""
                psiClass.superClass ?: ""
            }

            "implementsOf" -> {
                val psiClass = searchClass(project, firstArgument) ?: return emptyList<String>()
                psiClass.implementsList?.referencedTypes ?: emptyList<String>()
            }

            else -> ""
        }
    }

    private fun searchClass(project: Project, className: String): PsiClass? {
        val scope = GlobalSearchScope.allScope(project)
        val psiFacade = JavaPsiFacade.getInstance(project)
        return psiFacade.findClass(className, scope)
    }
}
