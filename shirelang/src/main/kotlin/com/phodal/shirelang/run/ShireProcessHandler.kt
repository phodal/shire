package com.phodal.shirelang.run

import com.intellij.build.process.BuildProcessHandler
import com.intellij.openapi.diagnostic.logger
import java.io.OutputStream

class ShireProcessHandler(private val myExecutionName: String) : BuildProcessHandler() {
    override fun detachIsDefault(): Boolean = true
    override fun destroyProcessImpl() = Unit
    override fun detachProcessImpl() {
        try {
            notifyProcessDetached()
        } catch (e: Exception) {
            // ignore
            logger<ShireProcessHandler>().warn(e)
        }
        finally {
            notifyProcessTerminated(0)
        }
    }

    fun exitWithError() = notifyProcessTerminated(-1)

    override fun getProcessInput(): OutputStream? = null
    override fun getExecutionName(): String = myExecutionName
}
