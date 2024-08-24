package com.phodal.shirelang.compiler.hobbit.execute

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class CrawlProcessorTest : BasePlatformTestCase() {
    fun testShouldParseLink() {
        val urls = arrayOf("https://shire.phodal.com/")
        val results = CrawlProcessor.crawl(urls)
        assertEquals(results.size, 1)
    }
}
