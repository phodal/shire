package com.phodal.shirelang.python.provider

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.python.PythonLanguage
import com.jetbrains.python.psi.PyClass
import com.jetbrains.python.psi.PyFile
import com.jetbrains.python.psi.PyFunction
import com.phodal.shirecore.provider.variable.PsiContextVariableProvider
import com.phodal.shirecore.provider.variable.impl.CodeSmellBuilder
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.phodal.shirelang.python.util.PyTestUtil
import com.phodal.shirelang.python.util.PythonPsiUtil

class ShirePythonPsiVariableProvider : PsiContextVariableProvider {
    override fun resolve(
        variable: PsiContextVariable,
        project: Project,
        editor: Editor,
        psiElement: PsiElement?,
    ): String {
        if (psiElement?.language !is PythonLanguage) return ""

        val underTestElement = PyTestUtil.getElementForTests(project, editor)
        val underTestFile = underTestElement?.containingFile as? PyFile ?: return ""

        return when (variable) {
            PsiContextVariable.CURRENT_CLASS_NAME -> {
                when (underTestElement) {
                    is PyClass -> underTestElement.name ?: ""
                    is PyFunction -> underTestElement.name ?: ""
                    else -> ""
                }
            }

            PsiContextVariable.CURRENT_CLASS_CODE -> {
                when (underTestElement) {
                    is PyClass -> underTestElement.text
                    is PyFunction -> underTestElement.text
                    else -> ""
                }
            }

            PsiContextVariable.CURRENT_METHOD_NAME -> {
                when (underTestElement) {
                    is PyFunction -> underTestElement.name ?: ""
                    else -> ""
                }
            }

            PsiContextVariable.CURRENT_METHOD_CODE -> {
                when (underTestElement) {
                    is PyFunction -> underTestElement.text
                    else -> ""
                }
            }

            PsiContextVariable.RELATED_CLASSES -> {
                when (underTestElement) {
                    is PyFunction -> {
                        PythonPsiUtil.findRelatedTypes(underTestElement).mapNotNull { it?.name ?: "" }
                    }

                    else -> listOf()
                }.joinToString("\n")
            }

            PsiContextVariable.SIMILAR_TEST_CASE -> TODO()
            PsiContextVariable.IMPORTS -> PythonPsiUtil.getImportsInFile(underTestFile)
            PsiContextVariable.IS_NEED_CREATE_FILE -> TODO()
            PsiContextVariable.TARGET_TEST_FILE_NAME -> {
                PyTestUtil.getTestNameExample(underTestFile.virtualFile)
            }

            PsiContextVariable.UNDER_TEST_METHOD_CODE -> TODO()
            PsiContextVariable.FRAMEWORK_CONTEXT -> return collectFrameworkContext(psiElement, project)
            PsiContextVariable.CODE_SMELL -> CodeSmellBuilder.collectElementProblemAsSting(
                underTestElement,
                project,
                editor
            )

            PsiContextVariable.METHOD_CALLER -> {
                PythonPsiUtil.collectAndResolveReferences(underTestElement)
            }

            PsiContextVariable.CALLED_METHOD -> {
                PythonPsiUtil.collectAndResolveReferences(underTestElement)
            }

            PsiContextVariable.SIMILAR_CODE -> TODO()
            PsiContextVariable.STRUCTURE -> TODO()
        }
    }
}
