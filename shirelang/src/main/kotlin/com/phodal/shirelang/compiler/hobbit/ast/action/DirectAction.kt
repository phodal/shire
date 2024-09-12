package com.phodal.shirelang.compiler.hobbit.ast.action

import com.phodal.shirelang.compiler.patternaction.PatternActionFunc

open class DirectAction(open val processors: List<PatternActionFunc>)