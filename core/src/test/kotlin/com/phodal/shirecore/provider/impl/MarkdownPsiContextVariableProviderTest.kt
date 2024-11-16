package com.phodal.shirecore.provider.impl

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirecore.provider.variable.model.PsiContextVariable
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.openapi.application.ReadAction

class MarkdownPsiContextVariableProviderTest: BasePlatformTestCase() {
    fun testShouldSuccessParseMarkdownHeading() {
        val markdownText = """# Hello World
            | sample
            | ## h2
            | ### h3
            | #### h4
        """.trimMargin()
        val html = MarkdownPsiContextVariableProvider().toHtml(markdownText)

        assertEquals("# Hello World\n##  h2\n###  h3\n####  h4", html)
    }

    fun testChangeCount() {
        val provider = MarkdownPsiContextVariableProvider()
        val psiElement = createPsiElement("sample.md", "# Hello World")
        val changeCount = provider.resolve(PsiContextVariable.CHANGE_COUNT, project, myFixture.editor, psiElement)
        assertEquals("0", changeCount)
    }

    fun testLineCount() {
        val provider = MarkdownPsiContextVariableProvider()
        val psiElement = createPsiElement("sample.md", "# Hello World\nsample\n## h2\n### h3\n#### h4")
        val lineCount = provider.resolve(PsiContextVariable.LINE_COUNT, project, myFixture.editor, psiElement)
        assertEquals("5", lineCount)
    }

    fun testComplexityCount() {
        val provider = MarkdownPsiContextVariableProvider()
        val psiElement = createPsiElement("sample.md", "# Hello World")
        val complexityCount = provider.resolve(PsiContextVariable.COMPLEXITY_COUNT, project, myFixture.editor, psiElement)
        assertEquals("0", complexityCount)
    }

    private fun createPsiElement(fileName: String, fileContent: String): PsiElement {
        val psiFile = myFixture.configureByText(fileName, fileContent)
        return ReadAction.compute<PsiFile, Throwable> {
            PsiManager.getInstance(project).findFile(psiFile.virtualFile)
        }!!
    }
}
