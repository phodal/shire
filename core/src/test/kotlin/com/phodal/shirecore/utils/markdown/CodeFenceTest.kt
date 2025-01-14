package com.phodal.shirecore.utils.markdown

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import kotlin.collections.get

class CodeFenceTest : BasePlatformTestCase() {

    fun testShould_handle_code_not_complete_from_markdown() {
        val markdown = """
            |```java
            |public class HelloWorld {
            |    public static void main(String[] args) {
            |        System.out.println("Hello, World");
        """.trimMargin()

        val code = CodeFence.parse(markdown)
        assertEquals(
            code.text, """
            |public class HelloWorld {
            |    public static void main(String[] args) {
            |        System.out.println("Hello, World");
        """.trimMargin()
        )
        assertTrue(!code.isComplete)
    }

    fun testShould_handle_code_for_when_code() {
        val markdown = """
            |下面是
            |```java
        """.trimMargin()

        val code = CodeFence.parse(markdown)
        assertEquals(
            code.text, """
        """.trimMargin()
        )
        assertTrue(!code.isComplete)
    }

    fun testShould_handle_pure_markdown_content() {
        val content = "```markdown\\nGET /wp/v2/posts\\n```"
        val code = CodeFence.parse(content)
        assertEquals(code.text, "GET /wp/v2/posts")
    }

    fun testShould_handle_http_request() {
        val content = "```http request\\nGET /wp/v2/posts\\n```"
        val code = CodeFence.parse(content)
        assertEquals(code.text, "GET /wp/v2/posts")
    }

    fun testShould_parse_code_from_markdown_java_hello_world() {
        val markdown = """
            |Java Hello, world
            |```java
            |public class HelloWorld {
            |    public static void main(String[] args) {
            |        System.out.println("Hello, World");
            |    }
            |}
            |```
            |
            |Python Hello, world
            |
            |```http request
            |DELETE /api/blog/1
            |Content-Type: application/json
        """.trimMargin()

        val codeFences = CodeFence.parseAll(markdown)

        assertEquals(codeFences.size, 4)

        val code = codeFences[1]

        assertEquals(
            code.text, """
            |public class HelloWorld {
            |    public static void main(String[] args) {
            |        System.out.println("Hello, World");
            |    }
            |}
        """.trimMargin()
        )

        assertTrue(code.isComplete)

        val last = codeFences.last()
        assertEquals(last.text, "DELETE /api/blog/1\nContent-Type: application/json")
        assertEquals(last.ideaLanguage.displayName, "HTTP Request")
        assertEquals(false, last.isComplete)
    }

    fun testShould_parse_code_for_http_request() {
        val markdown = """
            |Java Hello, world
            |```http request
            |GET /api
        """.trimMargin()

        val codeFences = CodeFence.parseAll(markdown)

        assertEquals(codeFences.size, 2)

        val last = codeFences.last()
        assertEquals(last.text, "GET /api")
        assertEquals(last.ideaLanguage.displayName, "HTTP Request")
        assertEquals(false, last.isComplete)
    }

    fun testShould_parse_code_for_empty_http_request() {
        val markdown = """
            |Java Hello, world
            |```http request
        """.trimMargin()

        val codeFences = CodeFence.parseAll(markdown)

        assertEquals(codeFences.size, 2)

        val last = codeFences.last()
        assertEquals(last.text, "")
        assertEquals(last.ideaLanguage.displayName, "HTTP Request")
        assertEquals(false, last.isComplete)
    }

    fun testSupportMultipleLanguage() {
        val markdown = """
            |Java Hello, world
            |```http request
            |GET /api
            |```
            |HELLO
        """.trimMargin()

        val codeFences = CodeFence.parseAll(markdown)

        assertEquals(codeFences.size, 3)

        val last = codeFences.last()
        assertEquals(last.text, "HELLO")
        assertEquals(last.ideaLanguage.displayName, "Markdown")
        assertEquals(true, last.isComplete)
    }


    fun testShouldParseHtmlCode() {
        val content = """
// patch to call tools for step 3 with DevIns language, should use DevIns code fence
<shire>
/patch:src/main/index.html
```patch
// the index.html code
```
</shire>
""".trimIndent()
        val code = CodeFence.parse(content)
        assertEquals(
            code.text, """
/patch:src/main/index.html
```patch
// the index.html code
```
""".trimIndent()
        )
        assertTrue(code.isComplete)
    }

    fun testShouldParseUndoneHtmlCode() {
        val content = """
// patch to call tools for step 3 with DevIns language, should use DevIns code fence
<shire>
/patch:src/main/index.html
```patch
// the index.html code
```
""".trimIndent()
        val code = CodeFence.parse(content)
        assertFalse(code.isComplete)
        assertEquals(
            code.text, """
/patch:src/main/index.html
```patch
// the index.html code
```
""".trimIndent()
        )
    }

    /// parse all with shires
    fun testShouldParseAllWithDevin() {
        val content = """
            |<shire>
            |// the index.html code
            |</shire>
            |
            |```java
            |public class HelloWorld {
            |    public static void main(String[] args) {
            |        System.out.println("Hello, World");
            |    }
            |}
            |```
        """.trimMargin()

        val codeFences = CodeFence.parseAll(content)
        assertEquals(codeFences.size, 3)
        assertEquals(
            codeFences[0].text, """
            |// the index.html code
        """.trimMargin()
        )
        assertTrue(codeFences[0].isComplete)
        assertEquals(
            codeFences[2].text, """
            |public class HelloWorld {
            |    public static void main(String[] args) {
            |        System.out.println("Hello, World");
            |    }
            |}
        """.trimMargin()
        )
        assertTrue(codeFences[2].isComplete)
    }
}
