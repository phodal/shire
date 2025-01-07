package com.phodal.shirelang.debugger

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.configurations.RunProfileState
import com.intellij.execution.configurations.RunnerSettings
import com.intellij.execution.executors.DefaultDebugExecutor
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.runners.GenericProgramRunner
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunContentDescriptor
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugProcessStarter
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XDebuggerManager
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator
import com.phodal.shirelang.run.ShireConfiguration

/// refs to: https://github.com/KronicDeth/intellij-elixir/pull/643/files#diff-b1ba5c87ca6f66a455e4c1539cb2d99a62722d067a3d9e8043b290426cea5470
class ShireDebugRunner : GenericProgramRunner<RunnerSettings>() {
    override fun canRun(executorId: String, profile: RunProfile): Boolean {
        return (executorId == DefaultDebugExecutor.EXECUTOR_ID) && profile is ShireConfiguration
    }

    override fun getRunnerId(): String = RUNNER_ID

    override fun doExecute(state: RunProfileState, environment: ExecutionEnvironment): RunContentDescriptor? {
        val xDebuggerManager = XDebuggerManager.getInstance(environment.project)
        return xDebuggerManager.startSession(environment, object : XDebugProcessStarter() {
            override fun start(session: XDebugSession): XDebugProcess {
                return ShireDebugProcess(session, environment)
            }
        }).runContentDescriptor
    }
}

class ShireDebugProcess(private val xDebugSession: XDebugSession, private val environment: ExecutionEnvironment) :
    XDebugProcess(xDebugSession) {
    override fun createConsole(): ExecutionConsole = debuggedExecutionResult.executionConsole

    private val debuggableConfiguration: ShireConfiguration get() = xDebugSession.runProfile as ShireConfiguration
    private val debuggedExecutionResult by lazy {
        environment.executor.let { executor ->
            debuggableConfiguration
                .getState(executor, environment)
                .execute(executor, environment.runner)!!
        }
    }

    private val breakpointHandlers = arrayOf<XBreakpointHandler<*>>(ShireBreakpointHandler(this))
    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> = breakpointHandlers

    fun addBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>) {

    }

    fun removeBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>) {

    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return ShireDebuggerEditorsProvider()
    }
}

class ShireBreakpointHandler(val process: ShireDebugProcess) :
    XBreakpointHandler<XLineBreakpoint<ShireBpProperties>>(ShireLineBreakpointType::class.java) {
    override fun registerBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>) {
        process.addBreakpoint(breakpoint)
    }

    override fun unregisterBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>, temporary: Boolean) {
        process.removeBreakpoint(breakpoint)
    }
}

class ShireBpProperties : XBreakpointProperties<ShireBpProperties>() {
    override fun getState(): ShireBpProperties = this
    override fun loadState(state: ShireBpProperties) {}
}


val RUNNER_ID: String = "ShireProgramRunner"