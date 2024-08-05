package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.variable.PsiQLInterpreter
import com.phodal.shirelang.compiler.hobbit.HobbitHole
import com.phodal.shirelang.compiler.hobbit.ast.MethodCall
import java.lang.reflect.Method
import java.util.*

class PsiQueryStatementProcessor(override val myProject: Project, hole: HobbitHole) :
    QueryStatementProcessor(myProject, hole) {

    override fun <T : Any> invokeMethodOrField(methodCall: MethodCall, element: T): Any? {
        val methodName = methodCall.methodName.display()
        val methodArgs = methodCall.arguments
        if (element is PsiElement) {
            PsiQLInterpreter.provide(element.language)?.let { psiQLInterpreter ->
                val hasPqlInterpreter = psiQLInterpreter.supportsMethod(element.language, methodName).filter {
                    it == methodName
                }.isNotEmpty()

                if (hasPqlInterpreter) {
                    return runReadAction {

                        psiQLInterpreter.resolveCall(element, methodName, methodCall.parameters() ?: emptyList())
                    }
                }
            }
        }

        val isField = methodArgs == null

        if (isField) {
            val field = element.javaClass.fields.find {
                it.name == methodName
            }

            if (field != null) {
                return field.get(element)
            }
        }

        // use reflection to call method
        val allMethods = element.javaClass.methods
        val method = allMethods.find {
            it.name == methodName
        }
        if (method != null) {
            if (methodArgs == null) {
                return method.invoke(element)
            }

            return method.invoke(element, methodArgs)
        }

        if (isField) {
            // maybe getter, we try to find getter, first upper case method name first letter
            val getterName = "get${
                methodName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
            }"
            val getter = allMethods.find {
                it.name == getterName
            }

            if (getter != null) {
                return getter.invoke(element)
            }
        }

        // if not found, show error log
        return showErrorLog(allMethods, element, methodName)
    }

    private fun <T : Any> showErrorLog(
        allMethods: Array<out Method>,
        element: T,
        methodName: String,
    ): Nothing? {
        val supportMethodNames: List<String> = allMethods.map { it.name }
        val supportFieldNames: List<String> = element.javaClass.fields.map { it.name }

        logger<PsiQueryStatementProcessor>().error(
            """
                method or field not found: $methodName
                supported methods: $supportMethodNames
                supported fields: $supportFieldNames
                """.trimIndent()
        )
        return null
    }
}
