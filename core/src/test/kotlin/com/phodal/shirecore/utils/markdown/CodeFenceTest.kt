package com.phodal.shirecore.utils.markdown

import com.intellij.testFramework.fixtures.BasePlatformTestCase

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
}