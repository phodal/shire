package com.phodal.shirecore.guard.model

import com.phodal.shirecore.guard.scanner.SecretPatternsScanner
import junit.framework.TestCase.*
import org.junit.Test

/**
 * Unit tests for the SecretPatternsManager class.
 */
class SecretPatternsScannerTest {

    private lateinit var secretPatterns: SecretPatternsScanner

    @Test
    fun `should add a new pattern to the list of patterns`() {
        // Given
        val newPattern = SecretPattern("Email", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "medium")
        secretPatterns = SecretPatternsScanner()

        // When
        secretPatterns.addPattern(newPattern)
        val updatedPatterns = secretPatterns.getPatterns()

        // Then
        assertTrue(updatedPatterns.contains(newPattern))
    }

    @Test
    fun `should remove a pattern from the list of patterns`() {
        // Given
        val patternToRemove = SecretPattern("Credit Card", "[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}", "high")
        secretPatterns = SecretPatternsScanner()

        // When
        secretPatterns.removePattern(patternToRemove)
        val remainingPatterns = secretPatterns.getPatterns()

        // Then
        assertFalse(remainingPatterns.contains(patternToRemove))
    }

    @Test
    fun `should find patterns that match the text`() {
        // Given
        val testText = "My email is example@example.com and my phone number is 123-456-7890."
        secretPatterns = SecretPatternsScanner()

        // When
        val output = secretPatterns.mask(testText)

        // Then
        assertEquals("My email is **** and my phone number is ****.", output)
    }
}
