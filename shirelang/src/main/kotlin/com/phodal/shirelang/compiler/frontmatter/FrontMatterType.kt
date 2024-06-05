package com.phodal.shirelang.compiler.frontmatter

enum class FrontMatterType(var data: List<String> = listOf()) {
    STRING,
    NUMBER,
    DATE,
    BOOLEAN,
    ARRAY,
    OBJECT
}
