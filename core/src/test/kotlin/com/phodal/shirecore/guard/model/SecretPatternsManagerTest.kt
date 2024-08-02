package com.phodal.shirecore.guard.model

import junit.framework.TestCase.*
import org.junit.Test

/**
 * Unit tests for the SecretPatternsManager class.
 */
class SecretPatternsManagerTest {

    private lateinit var secretPatternsManager: SecretPatternsManager

    @Test
    fun `should add a new pattern to the list of patterns`() {
        // Given
        val newPattern = SecretPattern("Email", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "medium")
        secretPatternsManager = SecretPatternsManager()

        // When
        secretPatternsManager.addPattern(newPattern)
        val updatedPatterns = secretPatternsManager.getPatterns()

        // Then
        assertTrue(updatedPatterns.contains(newPattern))
    }

    @Test
    fun `should remove a pattern from the list of patterns`() {
        // Given
        val patternToRemove = SecretPattern("Credit Card", "[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}", "high")
        secretPatternsManager = SecretPatternsManager()

        // When
        secretPatternsManager.removePattern(patternToRemove)
        val remainingPatterns = secretPatternsManager.getPatterns()

        // Then
        assertFalse(remainingPatterns.contains(patternToRemove))
    }

    @Test
    fun `should find patterns that match the text`() {
        // Given
        val testText = "My email is example@example.com and my phone number is 123-456-7890."
        secretPatternsManager = SecretPatternsManager()

        // When
        val output = secretPatternsManager.mask(testText)

        // Then
        assertEquals("My email is **** and my phone number is ****.", output)
    }
}
