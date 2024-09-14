// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.phodal.shire.marketplace

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.progress.PerformInBackgroundOption
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.util.Computable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.download.DownloadableFileService
import com.intellij.util.download.FileDownloader
import com.intellij.util.io.ZipUtil
import com.phodal.shire.ShireMainBundle
import com.phodal.shirecore.ShirelangNotifications
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.swing.SwingUtilities

class ShireDownloader(val project: Project, val item: ShirePackage) {
    private val downloadLock = ReentrantReadWriteLock()

    fun downloadAndUnzip(): Boolean {
        ShirelangNotifications.info(project, "Downloading ${item.name}")
        val dir = project.guessProjectDir()!!
        val service = DownloadableFileService.getInstance()

        val filename = item.url.substringAfterLast("/")
        val description = service.createFileDescription(
            item.url,
            filename
        )

        val downloader = service.createDownloader(listOf(description), "Download Shire package: " + item.name)

        if (SwingUtilities.isEventDispatchThread()) {
            ApplicationManager.getApplication()
                .executeOnPooledThread<Boolean> { downloadWithLock(downloader) }

            return true
        } else {
            return downloadWithLock(downloader)
        }
    }

    private fun downloadWithLock(downloader: FileDownloader): Boolean {
        downloadLock.writeLock().lock()
        try {
            val pluginDir: File = getPluginDir()
            return downloadWithProgress { doDownload(pluginDir, downloader) }
        } finally {
            downloadLock.writeLock().unlock()
        }
    }

    private fun downloadWithProgress(downloadTask: Computable<Boolean>): Boolean {
        if (ProgressManager.getInstance().hasProgressIndicator()) {
            return downloadTask.compute()
        } else {
            val indicator =
                BackgroundableProcessIndicator(
                    null, ShireMainBundle.message("downloading.package"),
                    PerformInBackgroundOption.ALWAYS_BACKGROUND, null, null, true
                )
            return ProgressManager.getInstance().runProcess(downloadTask, indicator)
        }
    }

    protected fun doDownload(pluginDir: File?, downloader: FileDownloader): Boolean {
        var tempDir: Path? = null
        try {
            tempDir = Files.createTempDirectory(".shire-download")
            val list = downloader.download(tempDir.toFile())
            val file = list[0].first
            ZipUtil.extract(file, getTargetDir(pluginDir), null)
            return true
        } catch (e: IOException) {
            val message = "Can't download Android Plugin component: " + item.name
            logger<ShireDownloader>().warn(message, e)
            ShirelangNotifications.error(project, e.message ?: message)
            return false
        } finally {
            if (tempDir != null) {
                FileUtil.delete(tempDir.toFile())
            }
        }
    }

    private fun getPluginDir(): File {
        val pluginDir = File(project.basePath, ".shire")
        if (!pluginDir.exists()) {
            pluginDir.mkdirs()

            return pluginDir
        }

        return pluginDir
    }

    private fun getTargetDir(pluginDir: File?): File {
        return pluginDir?.let { File(it, "") } ?: File(project.basePath, "")
    }
}