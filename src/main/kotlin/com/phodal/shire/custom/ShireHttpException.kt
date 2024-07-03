package com.phodal.shire.custom

class ShireHttpException(val error: String, private val statusCode: Int) : RuntimeException(error) {
    override fun toString(): String {
        return "ShireHttpException(error='$message', statusCode=$statusCode)"
    }
}