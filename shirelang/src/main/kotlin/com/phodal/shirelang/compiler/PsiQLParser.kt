package com.phodal.shirelang.compiler

import com.phodal.shirelang.compiler.hobbit.ast.FrontMatterType
import com.phodal.shirelang.psi.ShireQueryStatement

class PsiQLParser {
    companion object {
        fun parse(statement: ShireQueryStatement): FrontMatterType {
            return FrontMatterType.Variable("allController")
        }
    }

}
