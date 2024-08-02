package com.phodal.shirecore.guard.nlp

import com.phodal.shirecore.guard.base.LocalScanner

/**
 * Use local AI Model to scan the input, Only support for ONNX model + HF Tokenizer
 */
abstract class NlpScanner : LocalScanner {
    // handle with llm
    var modelPath: String = ""
    var modeName: String = ""

    fun initModel() {

    }
}