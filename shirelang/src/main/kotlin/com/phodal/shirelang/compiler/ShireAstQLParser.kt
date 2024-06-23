package com.phodal.shirelang.compiler

import com.phodal.shirelang.compiler.hobbit.ast.*
import com.phodal.shirelang.psi.ShireFromClause
import com.phodal.shirelang.psi.ShireQueryStatement
import com.phodal.shirelang.psi.ShireSelectClause
import com.phodal.shirelang.psi.ShireWhereClause

object ShireAstQLParser {
    fun parse(statement: ShireQueryStatement): FrontMatterType {
        return FrontMatterType.QueryStatement(
            ShirePsiQueryStatement(
                parseFrom(statement.fromClause),
                parseWhere(statement.whereClause),
                parseSelect(statement.selectClause)
            )
        )
    }

    private fun parseFrom(fromClause: ShireFromClause): List<VariableStatement> {
        return fromClause.psiElementDecl.variableDeclList.map {
            VariableStatement(it.psiType.identifier.text, it.identifier.text)
        }
    }

    private fun parseWhere(whereClause: ShireWhereClause): Statement {
        return FrontmatterParser.parseExpr(whereClause.expr)
    }

    private fun parseSelect(selectClause: ShireSelectClause): List<Statement> {
        return selectClause.exprList.map {
            FrontmatterParser.parseExpr(it)
        }
    }
}
