package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.phodal.shirelang.compiler.hobbit.execute.processor.CrawlProcessor

class CrawlProcessorTest : BasePlatformTestCase() {
    fun testShouldParseLink() {
        val urls = arrayOf("https://shire.phodal.com/")
        val results = CrawlProcessor.execute(urls)
        assertEquals(results.size, 1)
    }
}
