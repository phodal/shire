/**
 * The MIT License (MIT)
 * <p>
 *     https://github.com/nikolaikopernik/code-complexity-plugin
 *  </p>
 */
package com.phodal.shirelang.java.complexity

import com.intellij.psi.JavaTokenType
import com.intellij.psi.PsiBreakStatement
import com.intellij.psi.PsiCatchSection
import com.intellij.psi.PsiConditionalExpression
import com.intellij.psi.PsiContinueStatement
import com.intellij.psi.PsiDoWhileStatement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiForStatement
import com.intellij.psi.PsiForeachStatement
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiKeyword
import com.intellij.psi.PsiLambdaExpression
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiParenthesizedExpression
import com.intellij.psi.PsiPolyadicExpression
import com.intellij.psi.PsiPrefixExpression
import com.intellij.psi.PsiSwitchStatement
import com.intellij.psi.PsiWhileStatement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.tree.IElementType
import com.phodal.shirecore.ast.ComplexitySink
import com.phodal.shirecore.ast.ComplexityVisitor
import com.phodal.shirecore.ast.PointType
import com.phodal.shirecore.ast.PointType.*

class JavaLanguageVisitor(private val sink: ComplexitySink) : ComplexityVisitor() {
    override fun processElement(element: PsiElement) {
        when (element) {
            is PsiWhileStatement -> sink.increaseComplexityAndNesting(LOOP_WHILE)
            is PsiDoWhileStatement -> sink.increaseComplexityAndNesting(LOOP_WHILE)
            is PsiIfStatement -> element.processIfExpression()
            is PsiKeyword -> {
                if (element.text == PsiKeyword.ELSE && element.parent is PsiIfStatement) {
                    sink.increaseComplexity(ELSE)
                }
            }

            is PsiConditionalExpression -> {
                sink.increaseComplexityAndNesting(IF)
                element.calculateBinaryComplexity()
            }

            is PsiSwitchStatement -> sink.increaseComplexityAndNesting(SWITCH)
            is PsiForStatement -> sink.increaseComplexityAndNesting(LOOP_FOR)
            is PsiForeachStatement -> sink.increaseComplexityAndNesting(LOOP_FOR)
            is PsiCatchSection -> sink.increaseComplexityAndNesting(CATCH)
            is PsiBreakStatement -> if (element.labelIdentifier != null) sink.increaseComplexity(BREAK)
            is PsiContinueStatement -> if (element.labelIdentifier != null) sink.increaseComplexity(CONTINUE)
            is PsiLambdaExpression -> sink.increaseNesting()
            is PsiPolyadicExpression -> {
                // this method will go over all the nested elements as well
                // we don't want that so we accept only the top-level expressions
                // and the entire expression will be processed recursively in [calculateBinaryComplexity]
                if (element.parent !is PsiExpression) {
                    element.calculateBinaryComplexity()
                }
            }

            is PsiMethodCallExpression -> if (element.isRecursion()) sink.increaseComplexity(RECURSION)
        }
    }

    private fun PsiExpression.calculateBinaryComplexity() {
        var prevOperand: IElementType? = null
        this.children.forEach { element ->
            when (element) {
                is PsiJavaToken -> if (element.tokenType in listOf(JavaTokenType.ANDAND, JavaTokenType.OROR)) {
                    if (prevOperand == null || element.tokenType != prevOperand) {
                        sink.increaseComplexity(element.tokenType.toPointType())
                    }
                    prevOperand = element.tokenType
                }

                is PsiParenthesizedExpression -> {
                    element.calculateBinaryComplexity()
                    prevOperand = null
                }

                is PsiPrefixExpression -> {
                    element.calculateBinaryComplexity()
                    prevOperand = null
                }

                is PsiPolyadicExpression -> element.calculateBinaryComplexity()
            }
        }
    }

    override fun postProcess(element: PsiElement) {
        if (element is PsiWhileStatement ||
            element is PsiDoWhileStatement ||
            element is PsiConditionalExpression ||
            element is PsiForStatement ||
            element is PsiForeachStatement ||
            element is PsiCatchSection ||
            element is PsiSwitchStatement ||
            element is PsiLambdaExpression
        ) {
            sink.decreaseNesting()
        } else if (element is PsiIfStatement && !element.isElseIf()) {
            sink.decreaseNesting()
        }
    }

    override fun shouldVisitElement(element: PsiElement): Boolean = true

    private fun PsiIfStatement.processIfExpression() {
        // if exists `else` that is not a plain IF -> ignoring
        if (this.isElseIf()) {
            return
        }
        sink.increaseComplexityAndNesting(IF)
    }
}

/**
 * Checking if recursion is used.
 * Same problems as in [KtLanguageVisitor]
 */
private fun PsiMethodCallExpression.isRecursion(): Boolean {
    val parentMethod: PsiMethod = this.findCurrentMethod() ?: return false
    if (this.methodExpression.text != parentMethod.nameIdentifier?.text) return false
    if (this.argumentList.expressionCount != parentMethod.parameterList.parametersCount) return false
    return true
}

private fun PsiElement.findCurrentMethod(): PsiMethod? {
    var element: PsiElement? = this
    while (element != null && element !is PsiMethod) element = element.parent
    return element?.let { it as PsiMethod }
}

private fun PsiIfStatement.isElseIf(): Boolean =
    this.prevNotWhitespace().isElse()

private fun PsiElement?.isElse(): Boolean = this?.let {
    it is PsiKeyword && it.text == PsiKeyword.ELSE
} ?: false

private fun PsiIfStatement.prevNotWhitespace(): PsiElement? {
    var prev: PsiElement = this
    while (prev.prevSibling != null) {
        prev = prev.prevSibling
        if (prev !is PsiWhiteSpace) {
            return prev
        }
    }
    return null
}


private fun IElementType.toPointType(): PointType =
    when (this) {
        JavaTokenType.OROR -> LOGICAL_OR

        JavaTokenType.ANDAND -> LOGICAL_AND

        else -> UNKNOWN
    }
