package com.phodal.shirelang.compiler.parser

import com.phodal.shirelang.compiler.ast.FrontMatterType
import com.phodal.shirelang.compiler.ast.ShirePsiQueryStatement
import com.phodal.shirelang.compiler.ast.Statement
import com.phodal.shirelang.compiler.ast.VariableElement
import com.phodal.shirelang.psi.ShireFromClause
import com.phodal.shirelang.psi.ShireQueryStatement
import com.phodal.shirelang.psi.ShireSelectClause
import com.phodal.shirelang.psi.ShireWhereClause

object ShireAstQLParser {
    fun parse(statement: ShireQueryStatement): FrontMatterType {
        val value = ShirePsiQueryStatement(
            parseFrom(statement.fromClause),
            parseWhere(statement.whereClause)!!,
            parseSelect(statement.selectClause)
        )

        return FrontMatterType.QUERY_STATEMENT(value)
    }

    private fun parseFrom(fromClause: ShireFromClause): List<VariableElement> {
        return fromClause.psiElementDecl.psiVarDeclList.map {
            VariableElement(it.psiType.identifier.text, it.identifier.text)
        }
    }

    private fun parseWhere(whereClause: ShireWhereClause): Statement? {
        return HobbitHoleParser.parseExpr(whereClause.expr)
    }

    private fun parseSelect(selectClause: ShireSelectClause): List<Statement> {
        return selectClause.exprList.mapNotNull {
            HobbitHoleParser.parseExpr(it)
        }
    }
}
