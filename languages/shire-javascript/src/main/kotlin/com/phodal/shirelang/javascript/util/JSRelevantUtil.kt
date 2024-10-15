package com.phodal.shirelang.javascript.util

import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.lang.javascript.psi.ecma6.TypeScriptInterface
import com.intellij.lang.javascript.psi.ecma6.TypeScriptSingleType
import com.intellij.lang.javascript.psi.ecmal4.JSClass
import com.intellij.lang.javascript.psi.util.JSStubBasedPsiTreeUtil
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.logger
import com.intellij.psi.PsiElement
import com.phodal.shirecore.provider.codemodel.model.ClassStructure
import com.phodal.shirelang.javascript.codemodel.JavaScriptClassStructureProvider

object JSRelevantUtil {
    fun lookupRelevantClass(element: PsiElement): List<ClassStructure> {
        return ReadAction.compute<List<ClassStructure>, Throwable> {
            val elements = mutableListOf<ClassStructure>()
            when (element) {
                is JSClass -> {
                    element.functions.map {
                        elements += resolveByFunction(it).values
                    }
                }

                is JSFunction -> {
                    elements += resolveByFunction(element).values
                }

                else -> {}
            }

            return@compute elements
        }
    }

    private fun resolveByFunction(jsFunction: JSFunction): Map<String, ClassStructure> {
        val result = mutableMapOf<String, ClassStructure>()
        jsFunction.parameterList?.parameters?.map {
            it.typeElement?.let { typeElement ->
                result += resolveByType(typeElement, it.typeElement!!.text)
            }
        }

        result += jsFunction.returnTypeElement?.let {
            resolveByType(it, jsFunction.returnType!!.resolvedTypeText)
        } ?: emptyMap()

        return result
    }

    private fun resolveByType(
        returnType: PsiElement?,
        typeName: String,
    ): MutableMap<String, ClassStructure> {
        val result = mutableMapOf<String, ClassStructure>()
        when (returnType) {
            is TypeScriptSingleType -> {
                val resolveReferenceLocally = JSStubBasedPsiTreeUtil.resolveLocally(
                    typeName,
                    returnType
                )

                when (resolveReferenceLocally) {
                    is TypeScriptInterface -> {
                        JavaScriptClassStructureProvider().build(resolveReferenceLocally, false)?.let {
                            result += mapOf(typeName to it)
                        }
                    }

                    else -> {
                        logger<JSRelevantUtil>().warn("resolveReferenceLocally is not TypeScriptInterface: $resolveReferenceLocally")
                    }
                }
            }

            else -> {
                logger<JSRelevantUtil>().warn("returnType is not TypeScriptSingleType: $returnType")
            }
        }

        return result
    }
}