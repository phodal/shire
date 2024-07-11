package com.phodal.shirelang.java.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.phodal.shirecore.provider.variable.PsiQLInterpreter

class JavaPsiQLInterpreter : PsiQLInterpreter {
    /**
     * clazz.getName() or clazz.extensions
     */
    override fun resolveCall(element: PsiElement, methodName: String, arguments: List<String>): Any {
        // is of method
        if (methodName.endsWith("Of")) {
            return this.resolveOfTypedCall(element.project, methodName, arguments)
        }

        return when (element) {
            is PsiClass -> {
                when (methodName) {
                    "getName" -> element.name!!
                    "extends" -> element.extendsList?.referencedTypes?.map { it.name } ?: emptyList<String>()
                    "implements" -> element.implementsList?.referencedTypes?.map { it.name } ?: emptyList<String>()
                    "parentOf" -> element.superClass?.name ?: ""
                    else -> ""
                }
            }

            else -> ""
        }
    }

    override fun resolveOfTypedCall(project: Project, methodName: String, arguments: List<String>): Any {
        // get first argument for infer type
        val firstArgument = arguments.firstOrNull() ?: return ""

        return when (methodName) {
            "parentOf" -> {
                val psiClass = searchClass(project, firstArgument)
                psiClass?.superClass?.name ?: ""
            }

            "extendsOf" -> {
                val scope = GlobalSearchScope.allScope(project)
                val psiFacade = JavaPsiFacade.getInstance(project)
                val superClass = psiFacade.findClass(firstArgument, scope) ?: return emptyList<String>()

                val classes = psiFacade.findClasses(superClass.qualifiedName!!, scope)
                classes.filter { it.isInheritor(superClass, true) }.map { it.name }
            }

            "implementsOf" -> {
                val psiClass = searchClass(project, firstArgument) ?: return emptyList<String>()
                psiClass.implementsList?.referencedTypes?.map { it.name } ?: emptyList<String>()
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
