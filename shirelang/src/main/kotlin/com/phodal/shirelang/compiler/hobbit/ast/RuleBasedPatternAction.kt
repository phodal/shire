package com.phodal.shirelang.compiler.hobbit.ast

import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

class RuleBasedPatternAction(val pattern: String, val processors: List<PatternActionFunc>)