package com.phodal.shire.json

import com.intellij.json.psi.JsonObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ShireEnvVariableFillerTest {
    @Test
    fun should_return_original_message_body_when_json_object_is_null() {
        // Given
        val messageBody = "Hello, \${name}!"
        val variables = listOf(setOf("name"))
        val jsonObject: JsonObject? = null
        val processVars = mapOf("name" to "John")

        // When
        val result = ShireEnvVariableFiller.fillVariables(messageBody, variables, jsonObject, processVars)

        // Then
        assertThat(result).isEqualTo(messageBody)
    }
}
