package com.phodal.shirecore.middleware

/**
 * Post middleware actions, like
 * Logging, Metrics, CodeVerify, RunCode, ParseCode etc.
 *
 */
enum class PostProcessorType(var handleName: String) {
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
    VerifyCode("verifyCode"),

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

    /**
     * Save file to the disk
     */
    SaveFile("saveFile"),

    /**
     * Open file in the editor
     */
    OpenFile("openFile"),

    /**
     * Insert code to the editor by current cursor position.
     */
    InsertCode("insertCode"),

    /**
     * Format code
     */
    FormatCode("formatCode"),

    /**
     * Parse comment to the comment block
     */
    ParseComment("parseComment"),

    InsertNewline("insertNewline"),

    /**
     * Append text to the file
     */
    Append("append"),

    /**
     * Patch content to the file
     */
    Patch("patch"),

    /**
     * Diff
     */
    Diff("diff"),

    UpdateEditorText("updateEditorText"),

    // openWebpage
    OpenWebpage("openWebpage"),

    // showWebView
    ShowWebview("showWebView"),

    /**
     * Metric change count.
     */
    ChangeCountMetric("changeCountMetric"),

    /**
     * Metric line count.
     */
    LineCountMetric("lineCountMetric"),

    /**
     * Metric complexity count.
     */
    ComplexityMetric("complexityMetric"),
}
