package com.phodal.shirecore.guard.model

import com.intellij.testFramework.LightPlatformTestCase
import com.phodal.shirecore.function.guard.model.SecretPattern
import com.phodal.shirecore.function.guard.scanner.SecretPatternsScanner
import junit.framework.TestCase.*
import org.junit.Test

/**
 * Unit tests for the SecretPatternsManager class.
 */
class SecretPatternsScannerTest : LightPlatformTestCase() {

    //    fun `should add a new pattern to the list of patterns`() {
    fun testShouldAddNewPatternToListOfPatterns() {
        val secretPatterns: SecretPatternsScanner = SecretPatternsScanner(project)
        // Given
        val newPattern = SecretPattern("Email", "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", "medium")

        // When
        secretPatterns.addPattern(newPattern)
        val updatedPatterns = secretPatterns.getPatterns()

        // Then
        assertTrue(updatedPatterns.contains(newPattern))
    }

    //    fun `should remove a pattern from the list of patterns`() {
    fun testShouldRemovePatternFromListOfPatterns() {
        val secretPatterns: SecretPatternsScanner = SecretPatternsScanner(project)
        val patternToRemove = SecretPattern("Credit Card", "[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}", "high")

        // When
        secretPatterns.removePattern(patternToRemove)
        val remainingPatterns = secretPatterns.getPatterns()

        // Then
        assertFalse(remainingPatterns.contains(patternToRemove))
    }

    //    fun `should find patterns that match the text`() {
    fun testShouldFindPatternsThatMatchText() {
        val secretPatterns: SecretPatternsScanner = SecretPatternsScanner(project)
        val testText = "My email is example@example.com and my phone number is 123-456-7890."

        // When
        val output = secretPatterns.mask(testText)

        // Then
        assertEquals("My email is **** and my phone number is ****.", output)
    }
}
