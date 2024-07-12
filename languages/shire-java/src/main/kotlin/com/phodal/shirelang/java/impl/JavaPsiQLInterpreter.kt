package com.phodal.shirelang.java.impl

import com.intellij.ide.hierarchy.HierarchyBrowserBaseEx
import com.intellij.lang.Language
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.scope.packageSet.NamedScopesHolder
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.phodal.shirecore.provider.variable.PsiQLInterpreter
import com.phodal.shirecore.psi.JvmPsiPqlMethod


class JavaPsiQLInterpreter : PsiQLInterpreter {
    override fun supportsMethod(language: Language, methodName: String): List<String> {
        if (language.id != "JAVA") return emptyList()

        return JvmPsiPqlMethod.values().map { it.methodName }
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
                    JvmPsiPqlMethod.GET_NAME.methodName -> element.name!!
                    JvmPsiPqlMethod.NAME.methodName -> element.name!!
                    JvmPsiPqlMethod.EXTENDS.methodName -> {
                        val psiClasses =
                            element.extendsList?.referencedTypes?.map { it.resolve() } ?: emptyList<PsiClass>()
                        psiClasses
                    }

                    JvmPsiPqlMethod.IMPLEMENTS.methodName -> element.implementsList?.referencedTypes?.map { it.resolve() }
                        ?: emptyList<PsiClass>()

                    JvmPsiPqlMethod.METHOD_CODE_BY_NAME.methodName -> {
                        element.methods.firstOrNull { it.name == arguments.first() } ?: return ""
                    }

                    JvmPsiPqlMethod.FIELD_CODE_BY_NAME.methodName -> {
                        element.fields.firstOrNull { it.name == arguments.first() } ?: return ""
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
            JvmPsiPqlMethod.SUBCLASSES_OF.methodName -> {
                val facade = JavaPsiFacade.getInstance(project)

                val psiClass = facade.findClass(firstArgument, GlobalSearchScope.projectScope(project))
                if (psiClass == null) {
                    logger<JavaPsiQLInterpreter>().warn("Cannot find class: $firstArgument")
                    return ""
                }

                val map: List<PsiClass> =
                    ClassInheritorsSearch.search(psiClass, GlobalSearchScope.projectScope(project), true).map { it }
                map
            }

            JvmPsiPqlMethod.ANNOTATED_OF.methodName -> {
                val facade = JavaPsiFacade.getInstance(project)
                val annotationClass =
                    facade.findClass(firstArgument, GlobalSearchScope.allScope(project))

                if (annotationClass == null) {
                    logger<JavaPsiQLInterpreter>().warn("Cannot find annotation class: $firstArgument")
                    return ""
                }

                val classes = AnnotatedElementsSearch
                    .searchPsiClasses(annotationClass, GlobalSearchScope.projectScope(project))
                    .findAll()

                classes.toList()
            }

            JvmPsiPqlMethod.SUPERCLASS_OF.methodName -> {
                val psiClass = searchClass(project, firstArgument) ?: return ""
                psiClass.superClass ?: ""
            }

            JvmPsiPqlMethod.IMPLEMENTS_OF.methodName -> {
                val psiClass = searchClass(project, firstArgument) ?: return emptyList<String>()
                psiClass.implementsList?.referencedTypes ?: emptyList<String>()
            }

            else -> {
                logger<JavaPsiQLInterpreter>().error("Cannot find method: $methodName")
            }
        }
    }

    protected fun getSearchScope(myProject: Project, scopeType: String, thisClass: PsiElement?): SearchScope {
        var searchScope: SearchScope = GlobalSearchScope.allScope(myProject)

        when {
            HierarchyBrowserBaseEx.SCOPE_CLASS == scopeType -> {
                searchScope = LocalSearchScope(thisClass!!)
            }
            HierarchyBrowserBaseEx.SCOPE_MODULE == scopeType -> {
                val module = ModuleUtilCore.findModuleForPsiElement(thisClass!!)
                searchScope = module?.getModuleScope(true) ?: LocalSearchScope(thisClass!!)
            }
            HierarchyBrowserBaseEx.SCOPE_PROJECT == scopeType -> {
                searchScope = GlobalSearchScopesCore.projectProductionScope(myProject)
            }
            HierarchyBrowserBaseEx.SCOPE_TEST == scopeType -> {
                searchScope = GlobalSearchScopesCore.projectTestScope(myProject)
            }
            else -> {
                val namedScope = NamedScopesHolder.getScope(myProject, scopeType)
                if (namedScope != null) {
                    searchScope = GlobalSearchScopesCore.filterScope(myProject, namedScope)
                }
            }
        }

        return searchScope
    }

    private fun searchClass(project: Project, className: String): PsiClass? {
        val scope = GlobalSearchScope.allScope(project)
        val psiFacade = JavaPsiFacade.getInstance(project)
        return psiFacade.findClass(className, scope)
    }
}
