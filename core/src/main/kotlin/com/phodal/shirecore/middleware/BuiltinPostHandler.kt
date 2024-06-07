package com.phodal.shirecore.middleware

/**
 * Post middleware actions, like
 * Logging, Metrics, CodeVerify, RunCode, ParseCode etc.
 *
 */
enum class BuiltinPostHandler(var handleName: String) {
    /**
     * Logging the action.
     */
    Logging("logging"),

    /**
     * Metric time spent on the action.
     */
    TimeMetric("timeMetric"),

    /**
     * Acceptance metric.
     */
    AcceptanceMetric("acceptanceMetric"),

    /**
     * Check has code error or PSI issue.
     */
    CodeVerify("codeVerify"),

    /**
     * Run generate text code
     */
    RunCode("runCode"),

    /**
     * Parse text to code blocks
     */
    ParseCode("parseCode"),

    /**
     * For example, TestCode should be in the correct directory, like java test should be in test directory.
     */
    InferCodeLocation("InferCodeLocation"),
}