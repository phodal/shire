package com.phodal.shirecore.search.function

import com.phodal.shirecore.search.indices.normalized
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class FloatArrayExtensionTest {

    @Test
    fun should_return_normalized_float_array() {
        // Given
        val inputArray = floatArrayOf(1.0f, 2.0f, 3.0f)

        // When
        val normalizedArray = inputArray.normalized()

        // Then
        val expectedArray = floatArrayOf(0.26726124f, 0.5345225f, 0.8017837f)
        assertArrayEquals(expectedArray, normalizedArray, 0.0001f)
    }
}
