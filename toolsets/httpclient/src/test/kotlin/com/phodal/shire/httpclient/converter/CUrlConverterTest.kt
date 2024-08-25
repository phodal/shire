package com.phodal.shire.httpclient.converter


import com.intellij.testFramework.LightPlatformCodeInsightTestCase

class CUrlConverterTest : LightPlatformCodeInsightTestCase() {
    fun testShouldConvertCurlToRestClientRequest() {
        // Given
        val content = "curl -X POST http://example.com/api/resource -d 'data'"

        // When
        val restClientRequest = CUrlConverter.convert(project, content)

        // Then
        assertEquals("http://example.com/api/resource", restClientRequest.url)
    }
}
