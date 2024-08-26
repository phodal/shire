package com.phodal.shirecore.agent.agenttool.browse

import junit.framework.TestCase.assertNotNull
import org.junit.Test

class BrowseToolTest {
    @Test
    fun should_parseHtml_correctly() {
        // given
        val url = "https://shire.phodal.com"

        // when
        val result = BrowseTool.parse(url).body

        // then
        println(result)
        assertNotNull(result)
    }
}