package com.phodal.shirecore.middleware.builtin

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase

class FileNameTest : BasePlatformTestCase() {

    fun testReturnTimestampWithExtensionWhenFilePathIsBlank() {
        val ext = "txt"
        val filePath = ""
        val result = getValidFilePath(filePath, ext)

        // Check if the result matches a timestamp followed by the correct extension
        assertTrue(result.matches(Regex("""\d+\.txt""")))
    }

    fun testReturnValidFilePathIfItMatchesRegex() {
        val ext = "txt"
        val filePath = "C:\\Users\\John\\Documents\\file.txt"
        val result = getValidFilePath(filePath, ext)

        // Since the path is valid, it should return the same file path
        assertEquals(filePath, result)
    }

    fun testReturnTimestampWithExtensionIfPathIsInvalid() {
        val ext = "txt"
        val filePath = "Invalid\\Path\\test"
        val result = getValidFilePath(filePath, ext)

        // If the file path is invalid, it should return a timestamp followed by the extension
        assertTrue(result.matches(Regex("""\d+\.txt""")))
    }

    fun testReturnTimestampWithExtensionIfPathIsInvalidForLinux() {
        val ext = "txt"
        val filePath = "/home/user/Documents/file.shire"
        val result = getValidFilePath(filePath, ext)

        assertEquals(result, filePath)
    }

    fun testReturnParsedTextWhenFilePathContainsCodeFence() {
        val ext = "txt"
        val filePath = "```\nsome code block\n```"
        val parsedText = "some code block"

        // Mock or simulate the CodeFence.parse method
        val result = getValidFilePath(filePath, ext)

        // Assuming the CodeFence.parse method extracts "some code block"
        assertEquals(parsedText, result)
    }
}
