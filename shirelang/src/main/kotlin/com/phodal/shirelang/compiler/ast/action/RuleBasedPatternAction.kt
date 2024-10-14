package com.phodal.shirelang.compiler.ast.action

import com.phodal.shirelang.compiler.ast.patternaction.PatternActionFunc

class RuleBasedPatternAction(val pattern: String, override val processors: List<PatternActionFunc>) :
    DirectAction(processors)
