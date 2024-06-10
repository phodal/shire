package com.phodal.shirelang

import com.intellij.testFramework.ParsingTestCase
import com.phodal.shirelang.parser.ShireParserDefinition

class ShireParsingTest : ParsingTestCase("parser", "shire", ShireParserDefinition()) {
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testBasicTest() {
        doTest(true)
    }

    fun testJavaHelloWorld() {
        doTest(true)
    }

    fun testEmptyCodeFence() {
        doTest(true)
    }

    fun testJavaAnnotation() {
        doTest(true)
    }

    fun testBlockStartOnly() {
        doTest(true)
    }

    fun testComplexLangId() {
        doTest(true)
    }

    fun testAutoCommand() {
        doTest(true)
    }

    fun testCommandAndSymbol() {
        doTest(true)
    }

    fun testBrowseWeb() {
        doTest(true)
    }

    fun testAutoRefactor() {
        doTest(true)
    }

    fun testFrontMatter() {
        doTest(true)
    }

    fun testSingleComment() {
        doTest(true)
    }

    fun testSystemCalling() {
        doTest(true)
    }

    fun testShireFmObject() {
        doTest(true)
    }

    fun testPatternAction() {
        doTest(true)
    }

    fun testPatternCaseAction() {
        doTest(true)
    }
}

