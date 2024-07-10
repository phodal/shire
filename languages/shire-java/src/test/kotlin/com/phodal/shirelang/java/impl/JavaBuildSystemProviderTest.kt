package com.phodal.shirelang.java.impl

import org.junit.Assert.assertEquals
import org.junit.Test

class JavaTaskCompletionComparatorTest {

    @Test
    fun should_compare_when_both_start_with_double_dash() {
        // Given
        val o1 = "--task1"
        val o2 = "--task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(o1.compareTo(o2), result)
    }

    @Test
    fun should_compare_when_first_starts_with_dash_and_second_with_double_dash() {
        // Given
        val o1 = "-task1"
        val o2 = "--task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun should_compare_when_first_starts_with_double_dash_and_second_with_dash() {
        // Given
        val o1 = "--task1"
        val o2 = "-task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun should_compare_when_both_start_with_colon() {
        // Given
        val o1 = ":task1"
        val o2 = ":task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(o1.compareTo(o2), result)
    }

    @Test
    fun should_compare_when_first_starts_with_colon_and_second_with_dash() {
        // Given
        val o1 = ":task1"
        val o2 = "-task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun should_compare_when_first_starts_with_dash_and_second_with_colon() {
        // Given
        val o1 = "-task1"
        val o2 = ":task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun should_compare_when_second_starts_with_dash() {
        // Given
        val o1 = "task1"
        val o2 = "-task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun should_compare_when_second_starts_with_colon() {
        // Given
        val o1 = "task1"
        val o2 = ":task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun should_compare_when_first_starts_with_dash() {
        // Given
        val o1 = "-task1"
        val o2 = "task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun should_compare_when_first_starts_with_colon() {
        // Given
        val o1 = ":task1"
        val o2 = "task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(1, result)
    }

    @Test
    fun should_compare_when_neither_starts_with_dash_or_colon() {
        // Given
        val o1 = "task1"
        val o2 = "task2"

        // When
        val result = JAVA_TASK_COMPLETION_COMPARATOR.compare(o1, o2)

        // Then
        assertEquals(o1.compareTo(o2), result)
    }
}
