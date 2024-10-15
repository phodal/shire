package com.phodal.shirecore.sse.io

class ShireHttpException(val error: String, private val statusCode: Int) : RuntimeException(error) {
    override fun toString(): String {
        return "ShireHttpException(statusCode=$statusCode, message=$message)"
    }
}