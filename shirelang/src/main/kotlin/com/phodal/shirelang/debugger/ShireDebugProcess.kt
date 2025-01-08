package com.phodal.shirelang.debugger

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.execution.ui.layout.PlaceInGrid
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Key
import com.intellij.ui.content.Content
import com.intellij.util.Alarm
import com.intellij.xdebugger.XDebugProcess
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.ui.XDebugTabLayouter
import com.phodal.shirelang.psi.ShireFile
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.ShireConsoleView
import com.phodal.shirelang.run.ShireRunConfigurationProfileState
import com.phodal.shirelang.run.ShireRunListener
import com.phodal.shirelang.run.runner.ShireRunner
import com.phodal.shirelang.run.runner.ShireRunnerContext
import kotlinx.coroutines.runBlocking

class ShireDebugProcess(private val session: XDebugSession, private val environment: ExecutionEnvironment) :
    XDebugProcess(session), Disposable {
    private val debuggableConfiguration: ShireConfiguration get() = session.runProfile as ShireConfiguration
    private val runProfileState =
        debuggableConfiguration.getState(environment.executor, environment) as ShireRunConfigurationProfileState

    private val connection = ApplicationManager.getApplication().messageBus.connect(this)
    private val myRequestsScheduler: Alarm

    init {
        myRequestsScheduler = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
    }

    private val breakpointHandlers = arrayOf<XBreakpointHandler<*>>(ShireBreakpointHandler(this))

    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> = breakpointHandlers
    override fun createTabLayouter(): XDebugTabLayouter = ShireDebugTabLayouter(runProfileState.console)
    override fun createConsole(): ExecutionConsole = runProfileState.console

    var shireRunnerContext: ShireRunnerContext? = null


    fun start() {
        myRequestsScheduler.addRequest({
            runBlocking {
                val psiFile: ShireFile = ShireFile.lookup(session.project, debuggableConfiguration.getScriptPath())
                    ?: return@runBlocking

                shireRunnerContext = ShireRunner.compileOnly(session.project, psiFile, mapOf(), null)
                session.positionReached(ShireSuspendContext(this@ShireDebugProcess, session.project))
            }
        }, 0)
        connection.subscribe(ShireRunListener.TOPIC, object : ShireRunListener {
            override fun runFinish(
                allOutput: String,
                llmOutput: String,
                event: ProcessEvent,
                scriptPath: String,
                consoleView: ShireConsoleView?,
            ) {
                this@ShireDebugProcess.stop()
            }
        })

        runProfileState.console.print("Waiting for resume...", ConsoleViewContentType.NORMAL_OUTPUT)
    }

    override fun resume(context: XSuspendContext?) {
        runProfileState.execute(environment.executor, environment.runner)
    }

    override fun runToPosition(position: XSourcePosition, context: XSuspendContext?) {
        runProfileState.execute(environment.executor, environment.runner)
    }

    override fun startStepOut(context: XSuspendContext?) {
        runProfileState.execute(environment.executor, environment.runner)
    }

    override fun startStepInto(context: XSuspendContext?) {
        runProfileState.execute(environment.executor, environment.runner)
    }

    override fun startStepOver(context: XSuspendContext?) {
        runProfileState.execute(environment.executor, environment.runner)
    }

    override fun startForceStepInto(context: XSuspendContext?) {
        runProfileState.execute(environment.executor, environment.runner)
    }

    override fun startPausing() {

    }

    override fun stop() {
        connection.disconnect()
        processHandler.destroyProcess()
        session.stop()
    }

    override fun dispose() {
        connection.disconnect()
    }

    fun addBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>) {

    }

    fun removeBreakpoint(breakpoint: XLineBreakpoint<ShireBpProperties>) {

    }

    override fun getEditorsProvider(): XDebuggerEditorsProvider {
        return ShireDebuggerEditorsProvider()
    }
}

class ShireDebugTabLayouter(val console: ShireConsoleView) : XDebugTabLayouter() {
    override fun registerConsoleContent(ui: RunnerLayoutUi, console: ExecutionConsole): Content {
        val content = ui
            .createContent(
                "DebuggedConsoleContent", console.component, "Shire Debugged Console",
                AllIcons.Debugger.Console, console.preferredFocusableComponent
            )

        content.isCloseable = false
        ui.addContent(content, 1, PlaceInGrid.bottom, false)
        return content
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