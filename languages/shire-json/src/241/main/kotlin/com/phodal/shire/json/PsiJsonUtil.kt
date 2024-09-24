package com.phodal.shire.json

import com.intellij.json.JsonUtil
import com.intellij.json.psi.*

fun JsonProperty.valueAsString(obj: JsonObject): String? {
    val value = JsonUtil.getPropertyValueOfType(obj, name, JsonLiteral::class.java)
    return when (value) {
        is JsonStringLiteral -> value.value
        is JsonBooleanLiteral -> value.value.toString()
        else -> value?.text
    }
}

fun JsonObject.findString(name: String): String? {
    val property = findProperty(name) ?: return null
    return property.valueAsString(this)
}

fun JsonObject.findBoolean(name: String): Boolean? {
    val property = findProperty(name) ?: return null
    return JsonUtil.getPropertyValueOfType(this, name, JsonBooleanLiteral::class.java)?.value
}

fun JsonObject.findNumber(name: String): Number? {
    val property = findProperty(name) ?: return null
    return JsonUtil.getPropertyValueOfType(this, name, JsonNumberLiteral::class.java)?.value
}
