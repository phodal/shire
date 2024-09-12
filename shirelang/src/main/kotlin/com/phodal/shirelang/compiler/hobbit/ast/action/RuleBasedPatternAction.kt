package com.phodal.shirelang.compiler.hobbit.ast.action

import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

class RuleBasedPatternAction(val pattern: String, override val processors: List<PatternActionFunc>) :
    DirectAction(processors)
