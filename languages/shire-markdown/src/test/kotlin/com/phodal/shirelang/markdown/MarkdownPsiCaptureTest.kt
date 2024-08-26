import com.phodal.shirelang.markdown.MarkdownPsiCapture
import junit.framework.TestCase.assertEquals
import org.intellij.lang.annotations.Language
import org.junit.Test

class MarkdownPsiCaptureTest {

    @Test
    fun should_return_url_when_gfm_auto_link() {
        @Language("Markdown")
        val markdownText = """
            normal text link: https://shire.phodal.com
        """.trimIndent()

        val markdownPsiCapture = MarkdownPsiCapture()

        // when
        val result = markdownPsiCapture.capture(markdownText, "link")

        // then
        assertEquals("https://shire.phodal.com", result.first())
    }

    @Test
    fun should_return_url_when_markdown_label_link() {
        @Language("Markdown")
        val markdownText = """
            normal text link: [Shire](https://shire.phodal.com)
            link 2: https://aise.phodal.com
        """.trimIndent()
        val markdownPsiCapture = MarkdownPsiCapture()

        // when
        val result = markdownPsiCapture.capture(markdownText, "link")

        // then
        assertEquals("https://shire.phodal.com", result.first())
        assertEquals("https://aise.phodal.com", result[1])
    }

    @Test
    fun should_ignore_image_url() {
        @Language("Markdown")
        val markdownText = """
            hello sample 
            
            ![Shire](https://shire.phodal.com/images/pluginIcon.svg)
            """.trimIndent()
        val markdownPsiCapture = MarkdownPsiCapture()

        // when
        val result = markdownPsiCapture.capture(markdownText, "link")

        // then
        assertEquals(0, result.size)
    }

    @Test
    fun shouldParseLinkInList() {
        @Language("Markdown")
        val markdownText = """
1. [aichat](https://terminaltrove.com/aichat/) - <small>all-in-one AI powered CLI chat and copilot.</small>
2. [aider](https://terminaltrove.com/aider/) - <small>AI pair programming in your terminal</small>
3. [elia](https://terminaltrove.com/elia/) - <small>A TUI ChatGPT client built with Textual</small>
4. [gpterminator](https://terminaltrove.com/gpterminator/) - <small>A TUI for OpenAI's ChatGPT</small>
5. [gtt](https://terminaltrove.com/gtt/) - <small>A TUI for Google Translate, ChatGPT, DeepL and other AI services.</small>
6. [nvitop](https://terminaltrove.com/nvitop/) - <small>An interactive NVIDIA-GPU process viewer and beyond.</small>
7. [nvtop](https://terminaltrove.com/nvtop/) - <small>NVIDIA GPUs htop like monitoring tool</small>
8. [ollama](https://terminaltrove.com/ollama/) - <small>get up and running with large language models locally.</small>
9. [oterm](https://terminaltrove.com/oterm/) - <small>A text-based terminal client for ollama.</small>
10. [tgpt](https://terminaltrove.com/tgpt/) - <small>AI Chatbots in the terminal without needing API keys.</small>
11. [yai](https://terminaltrove.com/yai/) - <small>Your AI powered terminal assistant</small>
        """.trimMargin()

        val markdownPsiCapture = MarkdownPsiCapture()

        val result = markdownPsiCapture.capture(markdownText, "link")

        assertEquals(11, result.size)
    }
}
