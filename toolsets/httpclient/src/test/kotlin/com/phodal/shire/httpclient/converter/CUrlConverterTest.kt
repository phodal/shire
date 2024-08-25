package com.phodal.shire.httpclient.converter


import com.intellij.testFramework.LightPlatformCodeInsightTestCase
import okhttp3.OkHttpClient



class CUrlConverterTest : LightPlatformCodeInsightTestCase() {
    fun testShouldConvertCurlToRestClientRequest() {
        // Given
        val content = "curl -X POST http://example.com/api/resource -d 'data'"

        // When
        val restClientRequest = CUrlConverter.convert(project, content)

        // Then
        assertEquals("http://example.com/api/resource", restClientRequest.url)
    }

    fun testShouldCallHttpClientWithConvertedRequest() {
        // Given
        val content = "curl --location 'https://open.bigmodel.cn/api/paas/v4/chat/completions' \\\n" +
                "--header 'Authorization: Bearer \$YourKey' \\\n" +
                "--header 'Content-Type: application/json' \\\n" +
                "--data '{\n" +
                "    \"model\": \"glm-4\",\n" +
                "    \"messages\": [\n" +
                "        {\n" +
                "            \"role\": \"user\",\n" +
                "            \"content\": \"你好\"\n" +
                "        }\n" +
                "    ]\n" +
                "}'"

        // When
        val request = CUrlConverter.convert(project, content)

        // Then
        assertEquals("https://open.bigmodel.cn/api/paas/v4/chat/completions", request.url.toString())
//        val client = OkHttpClient()
//        val response = client.newCall(request).execute()
//        assertEquals(401, response.code)
    }
}
