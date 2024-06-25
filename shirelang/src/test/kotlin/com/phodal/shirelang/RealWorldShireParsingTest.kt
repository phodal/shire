package com.phodal.shirelang

import com.intellij.testFramework.ParsingTestCase
import com.phodal.shirelang.parser.ShireParserDefinition

class RealWorldShireParsingTest : ParsingTestCase("realworld", "shire", ShireParserDefinition()) {
    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    fun testAutotest() {
        doTest(true)
    }
}

