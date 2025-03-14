package com.phodal.shirelang

import com.intellij.testFramework.ParsingTestCase
import com.phodal.shirelang.parser.ShireParserDefinition

class ParsingRealWorldTest : ParsingTestCase("realworld", "shire", ShireParserDefinition()) {
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testAutotest() {
        doTest(true)
    }

    fun testLifeCycle() {
        doTest(true)
    }

    fun testContentTee() {
        doTest(true)
    }

    fun testWhenAfterStreaming() {
        doTest(true)
    }

    fun testAfterStreamingOnly() {
        doTest(true)
    }

    fun testOutputInVariable() {
        doTest(true)
    }

    fun testOnPaste() {
        doTest(true)
    }

    fun testLoginCommit() {
        doTest(true)
    }
}

