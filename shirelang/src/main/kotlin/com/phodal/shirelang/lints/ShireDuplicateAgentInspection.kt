package com.phodal.shirelang.lints

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.util.elementType
import com.phodal.shirelang.ShireBundle
import com.phodal.shirelang.psi.ShireTypes
import com.phodal.shirelang.psi.ShireUsed
import com.phodal.shirelang.psi.ShireVisitor

class ShireDuplicateAgentInspection : LocalInspectionTool() {
    override fun getGroupDisplayName() = ShireBundle.message("inspection.group.name")

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return ShireDuplicateAgentVisitor(holder)
    }

    private class ShireDuplicateAgentVisitor(val holder: ProblemsHolder) : ShireVisitor() {
        private var agentIds: MutableSet<ShireUsed> = mutableSetOf()

        override fun visitUsed(o: ShireUsed) {
            if (o.firstChild.nextSibling.elementType == ShireTypes.AGENT_ID) {
                agentIds.add(o)

                if (agentIds.contains(o)) {
                    agentIds.forEachIndexed { index, it ->
                        if (index > 0) {
                            holder.registerProblem(it, ShireBundle.message("inspection.duplicate.agent"))
                        }
                    }
                }
            }
        }
    }   
}
