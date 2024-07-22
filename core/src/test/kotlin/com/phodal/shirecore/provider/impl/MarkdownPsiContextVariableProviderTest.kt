package com.phodal.shirecore.provider.impl

import com.intellij.testFramework.fixtures.BasePlatformTestCase

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
}