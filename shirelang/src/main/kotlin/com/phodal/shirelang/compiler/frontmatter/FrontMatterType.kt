package com.phodal.shirelang.compiler.frontmatter

sealed class FrontMatterType(val value: Any) {
    class STRING(value: String): FrontMatterType(value)
    class NUMBER(value: Int): FrontMatterType(value)
    class DATE(value: String): FrontMatterType(value)
    class BOOLEAN(value: Boolean): FrontMatterType(value)
    class ARRAY(value: List<FrontMatterType>): FrontMatterType(value)
    class OBJECT(value: Map<String, FrontMatterType>): FrontMatterType(value)
}
