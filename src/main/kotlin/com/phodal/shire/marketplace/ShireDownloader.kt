package com.phodal.shire.marketplace

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectBundle
import com.intellij.openapi.project.guessProjectDir
import com.intellij.openapi.projectRoots.impl.jdkDownloader.JdkInstaller
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFileManager
import com.phodal.shire.ShireMainBundle
import com.phodal.shirecore.ShirelangNotifications
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ShireDownloader {
    fun downloadAndUnzip(project: Project, item: ShirePackage) {
        ShirelangNotifications.info(project, "Downloading ${item.name}")
        computeInBackground(project, ShireMainBundle.message("downloading.item", item.name)) {
            val dir = project.guessProjectDir()!!
            val targetPath = Paths.get(dir.path, ".")

            val zipFile = Files.createTempFile("temp_download", ".zip")
            downloadFile(item.url, zipFile.toString())

            unzipFile(zipFile.toString(), targetPath.toString())

            WriteCommandAction.runWriteCommandAction(null) {
                VirtualFileManager.getInstance().syncRefresh()
            }
        }

        ShirelangNotifications.info(project, "Downloaded ${item.name}")
    }

    private fun downloadFile(downloadUrl: String, outputFilePath: String) {
        URL(downloadUrl).openStream().use { inputStream ->
            Files.newOutputStream(Paths.get(outputFilePath)).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    private fun unzipFile(zipFilePath: String, targetDirPath: String) {
        ZipInputStream(BufferedInputStream(Files.newInputStream(Paths.get(zipFilePath)))).use { zis ->
            var entry: ZipEntry?
            while (zis.nextEntry.also { entry = it } != null) {
                val filePath = Paths.get(targetDirPath, entry!!.name)
                if (entry!!.isDirectory) {
                    Files.createDirectories(filePath)
                } else {
                    Files.createDirectories(filePath.parent)
                    FileOutputStream(filePath.toFile()).use { outputStream ->
                        zis.copyTo(outputStream)
                    }
                }
            }
        }
    }

    private inline fun <T : Any?> computeInBackground(
        project: Project?,
        @NlsContexts.DialogTitle title: String,
        crossinline action: (ProgressIndicator) -> T,
    ): T =
        ProgressManager.getInstance().run(object : Task.WithResult<T, Exception>(project, title, true) {
            override fun compute(indicator: ProgressIndicator) = action(indicator)
        })
}