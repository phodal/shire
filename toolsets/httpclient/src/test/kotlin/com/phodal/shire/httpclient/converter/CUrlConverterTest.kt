package com.phodal.shire.httpclient.converter

import com.intellij.psi.search.ProjectScope
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.indexing.FileBasedIndex
import com.phodal.shirecore.index.SHIRE_ENV_ID

class CUrlConverterTest : BasePlatformTestCase() {
    fun testShouldConvertCurlToRestClientRequest() {
        // Given
        val content = "curl -X POST http://example.com/api/resource -d 'data'"

        // When
        val restClientRequest = CUrlConverter.convert(content)

        // Then
        assertEquals("http://example.com/api/resource", restClientRequest.url.toString())
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
        val request = CUrlConverter.convert(content)

        // Then
        assertEquals("https://open.bigmodel.cn/api/paas/v4/chat/completions", request.url.toString())
//        val client = OkHttpClient()
//        val response = client.newCall(request).execute()
//        assertEquals(401, response.code)
    }

//    fun testShouldHandleForVariable() {
//        val jsonEnv = """
//            {
//              "development": {
//                "name": "Phodal"
//              }
//            }
//             """.trimIndent()
//
//        myFixture.addFileToProject("demo.shireEnv.json", jsonEnv)
//
//        val variables: MutableList<Set<String>> = FileBasedIndex.getInstance().getValues(
//            SHIRE_ENV_ID,
//            "development",
//            ProjectScope.getContentScope(project)
//        )
//
//        // Given
//        val messageBody = "Hello \${name}!"
//
//        // When
//        val result = CUrlConverter.fillVariables(messageBody,  variables)
//
//        // Then
//        assertEquals("Hello !", result)
//    }
}
