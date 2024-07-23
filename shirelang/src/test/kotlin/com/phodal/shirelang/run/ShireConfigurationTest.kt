// File: com/phodal/shirelang/run/ShireConfiguration Companion Test.kt

package com.phodal.shirelang.run

import com.phodal.shirelang.run.ShireConfiguration.Companion.mapStringToMap
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ShireConfigurationCompanionTest {

    @Test
    fun should_mapSimpleStringToMap_correctly() {
        // given
        val varMapString = "{key1=value1, key2=value2}"

        // when
        val result = mapStringToMap(varMapString)

        // then
        assertEquals(mapOf("key1" to "value1", "key2" to "value2"), result)
    }

    @Test
    fun should_mapEmptyStringToEmptyMap() {
        // given
        val varMapString = "{}"

        // when
        val result = mapStringToMap(varMapString)

        // then
        assertEquals(emptyMap<String, String>(), result)
    }

    @Test
    fun should_handleMapWithMultipleValuesPerKey_correctly() {
        // given
        val varMapString = "{key1=value1, key1=value2}"

        // when
        val result = mapStringToMap(varMapString)

        // then
        assertEquals(mapOf("key1" to "value2"), result)
        // Note: As per current implementation, the last value for a key will replace the previous values.
    }

    @Test
    fun shouldTransformFromMapAndToString() {
        // given
        val varMap = mapOf("key1" to "value1", "key2" to "value2")

        // when
        val result = mapStringToMap(varMap.toString())

        // then
        assertEquals(varMap, result)
    }
}
