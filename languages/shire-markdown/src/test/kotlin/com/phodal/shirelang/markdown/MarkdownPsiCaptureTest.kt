import com.phodal.shirelang.markdown.MarkdownPsiCapture
import junit.framework.TestCase.assertEquals
import org.junit.Test

class MarkdownPsiCaptureTest {

    @Test
    fun should_return_url_when_gfm_auto_link() {
        // given
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
        // given
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
        // given
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
}
