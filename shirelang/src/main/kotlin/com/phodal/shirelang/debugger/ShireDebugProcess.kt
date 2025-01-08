package com.phodal.shirelang.debugger

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ExecutionConsole
import com.intellij.execution.ui.RunnerLayoutUi
import com.intellij.execution.ui.layout.PlaceInGrid
import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Key
import com.intellij.ui.content.Content
import com.intellij.util.Alarm
import com.intellij.xdebugger.*
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XBreakpointProperties
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider
import com.intellij.xdebugger.frame.XSuspendContext
import com.intellij.xdebugger.ui.XDebugTabLayouter
import com.phodal.shirelang.run.ShireConfiguration
import com.phodal.shirelang.run.ShireConsoleView
import com.phodal.shirelang.run.ShireRunListener

class ShireDebugProcess(private val session: XDebugSession, private val environment: ExecutionEnvironment) :
    XDebugProcess(session), Disposable {
    private val connection = ApplicationManager.getApplication().messageBus.connect(this)
    private val myRequestsScheduler: Alarm

    init {
        session.addSessionListener(object : XDebugSessionListener {
            override fun stackFrameChanged() {

            }

            override fun sessionPaused() {

            }
        })

        session.positionReached(ShireSuspendContext(this@ShireDebugProcess, session.project))
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

        myRequestsScheduler = Alarm(Alarm.ThreadToUse.POOLED_THREAD, this)
    }

    private val debuggableConfiguration: ShireConfiguration get() = session.runProfile as ShireConfiguration

    private val breakpointHandlers = arrayOf<XBreakpointHandler<*>>(ShireBreakpointHandler(this))
    override fun getBreakpointHandlers(): Array<XBreakpointHandler<*>> = breakpointHandlers

    override fun createTabLayouter(): XDebugTabLayouter = ShireDebugTabLayouter()

    override fun resume(context: XSuspendContext?) {

    }

    fun start() {
        environment.executor.let { executor ->
            debuggableConfiguration
                .getState(executor, environment)
                .execute(executor, environment.runner)!!
        }

        processHandler.addProcessListener(object : ProcessListener {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                super.onTextAvailable(event, outputType)
            }

            override fun processTerminated(event: ProcessEvent) {
                super.processTerminated(event)
                stop()
            }
        })
    }

    override fun startStepOut(context: XSuspendContext?) {

    }

    override fun startStepInto(context: XSuspendContext?) {

    }

    override fun startStepOver(context: XSuspendContext?) {

    }

    override fun startForceStepInto(context: XSuspendContext?) {

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

class ShireDebugTabLayouter : XDebugTabLayouter() {
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