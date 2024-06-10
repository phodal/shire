package com.phodal.shirelang.compiler.hobbit

class ShirePatternAction(val pattern: String, val processors: List<PatternFun>)

sealed class FrontMatterType(val value: Any) {
    class STRING(value: String): FrontMatterType(value)
    class NUMBER(value: Int): FrontMatterType(value)
    class DATE(value: String): FrontMatterType(value)
    class BOOLEAN(value: Boolean): FrontMatterType(value)
    class ARRAY(value: List<FrontMatterType>): FrontMatterType(value)
    class OBJECT(value: Map<String, FrontMatterType>): FrontMatterType(value)

    /**
     * The default pattern action handles for processing
     */
    class PATTERN(value: ShirePatternAction): FrontMatterType(value)

    /**
     * The case match for the front matter.
     */
    class CaseMatch(value: Map<String, PATTERN>): FrontMatterType(value)
}
