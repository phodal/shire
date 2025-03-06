package com.phodal.shirelang.compiler.execute.command

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtilRt
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.FileStatusManager
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.phodal.shirecore.lookupFile
import com.phodal.shirelang.completion.dataprovider.BuiltinCommand

/**
 * The `DirShireCommand` class is responsible for listing files and directories in a tree-like structure for a given directory path within a project.
 * It implements the `ShireCommand` interface and provides an `execute` method to perform the directory listing operation asynchronously.
 *
 * The tree structure is visually represented using indentation and symbols (`├──`, `└──`) to denote files and subdirectories. Files are listed
 * first, followed by subdirectories, which are recursively processed to display their contents.
 *
 * Example output:
 * ```
 * myDirectory/
 *   ├── file1.txt
 *   ├── file2.txt
 *   └── subDirectory/
 *       ├── file3.txt
 *       └── subSubDirectory/
 *           └── file4.txt
 * ```
 *
 * @param myProject The project instance in which the directory resides.
 * @param dir The path of the directory to list.
 */
class DirShireCommand(private val myProject: Project, private val dir: String) : ShireCommand {
    override val commandName = BuiltinCommand.DIR

    private val defaultMaxDepth = 2

    private sealed class TreeNode {
        abstract val name: String
        data class FileNode(override val name: String, val size: String?) : TreeNode()
        data class DirectoryNode(
            override val name: String, val children: MutableList<TreeNode> = mutableListOf()
        ) : TreeNode() {
            fun addChild(child: TreeNode) {
                children.add(child)
            }
        }
        data class CompressedNode(override val name: String, val subdirNames: List<String>) : TreeNode()
        data class ParallelDirsNode(
            override val name: String,
            val dirNames: List<String>,
            val commonChildName: String
        ) : TreeNode()
    }

    override suspend fun doExecute(): String? {
        val virtualFile = myProject.lookupFile(dir) ?: return "File not found: $dir"
        val psiDirectory = PsiManager.getInstance(myProject).findDirectory(virtualFile) ?: return null

        val rootNode = runReadAction { buildDirectoryTree(myProject, psiDirectory, 1) } ?: return null

        val output = StringBuilder().apply {
            appendLine("$dir/")
            renderTree(rootNode, 1, this)
        }

        return output.toString()
    }


    private fun buildDirectoryTree(project: Project, directory: PsiDirectory, depth: Int): TreeNode.DirectoryNode? {
        if (isExcluded(project, directory)) return null

        val dirNode = TreeNode.DirectoryNode(directory.name)

        if (depth <= defaultMaxDepth) {
            directory.files.forEach { file ->
                val fileSize = StringUtilRt.formatFileSize(file.virtualFile.length)
                dirNode.addChild(TreeNode.FileNode(file.name, fileSize))
            }
        }

        val subdirectories = directory.subdirectories.filter { !isExcluded(project, it) }
        val parallelDirsNode = detectParallelSimpleDirs(project, subdirectories)
        if (parallelDirsNode != null) {
            dirNode.addChild(parallelDirsNode)
            processRemainingDirs(project, subdirectories, parallelDirsNode.dirNames, dirNode, depth)
            return dirNode
        }

        if (shouldCompressSubdirectories(project, directory, subdirectories, depth)) {
            val compressableSubdirs = getCompressableSubdirectories(subdirectories)
            if (compressableSubdirs.isNotEmpty()) {
                dirNode.addChild(TreeNode.CompressedNode("compressed", compressableSubdirs.map { it.name }))
            }
        } else {
            subdirectories.forEach { subdir ->
                buildDirectoryTree(project, subdir, depth + 1)?.let { subdirNode ->
                    dirNode.addChild(subdirNode)
                }
            }
        }

        return dirNode
    }

    /**
     * 处理剩余的不符合并列目录模式的子目录
     */
    private fun processRemainingDirs(
        project: Project,
        allDirs: List<PsiDirectory>,
        parallelDirNames: List<String>,
        parentNode: TreeNode.DirectoryNode,
        depth: Int
    ) {
        val remainingDirs = allDirs.filter { dir -> dir.name !in parallelDirNames }
        remainingDirs.forEach { dir ->
            buildDirectoryTree(project, dir, depth + 1)?.let { subdirNode ->
                parentNode.addChild(subdirNode)
            }
        }
    }

    /**
     * 检测并列的简单目录模式，如多个组件目录下都只有一个相同名称的子目录
     */
    private fun detectParallelSimpleDirs(project: Project, subdirs: List<PsiDirectory>): TreeNode.ParallelDirsNode? {
        if (subdirs.size < 2) return null

        // 收集有相同子目录结构的目录组
        val dirGroups = mutableMapOf<String, MutableList<PsiDirectory>>()

        // 对每个目录，检查它是否有单一子目录，如果有，记录子目录名
        subdirs.forEach { dir ->
            val nonExcludedChildren = dir.subdirectories.filter { !isExcluded(project, it) }
            if (nonExcludedChildren.size == 1) {
                val childName = nonExcludedChildren.first().name
                dirGroups.getOrPut(childName) { mutableListOf() }.add(dir)
            }
        }

        // 找出最大的组（具有相同子目录名的父目录组）
        val largestGroup = dirGroups.maxByOrNull { it.value.size }

        // 如果最大组至少有2个目录且子目录名不为空，则创建并列目录节点
        if (largestGroup != null && largestGroup.value.size >= 2 && largestGroup.key.isNotEmpty()) {
            val commonChildName = largestGroup.key
            val parentDirNames = largestGroup.value.map { it.name }

            return TreeNode.ParallelDirsNode("parallelDirs", parentDirNames, commonChildName)
        }

        return null
    }

    /**
     * 判断是否应该压缩显示子目录
     */
    private fun shouldCompressSubdirectories(
        project: Project, directory: PsiDirectory, subdirectories: List<PsiDirectory>, depth: Int): Boolean {
        // 深度超过阈值且有多个子目录时考虑压缩
        return depth > defaultMaxDepth + 1 && subdirectories.size > 1 &&
                // 确保这些子目录大多是叶子节点或近似叶子节点
                subdirectories.all { subdir ->
                    val childDirs = subdir.subdirectories.filter { !isExcluded(project, it) }
                    childDirs.isEmpty() || childDirs.all { it.subdirectories.isEmpty() }
                }
    }

    /**
     * 获取可以压缩显示的子目录
     */
    private fun getCompressableSubdirectories(subdirectories: List<PsiDirectory>): List<PsiDirectory> {
        // 这里可以添加更复杂的逻辑来决定哪些目录可以压缩
        return subdirectories
    }

    /**
     * 将目录树渲染为文本输出
     */
    private fun renderTree(node: TreeNode, depth: Int, output: StringBuilder) {
        val indent = " ".repeat(depth)

        when (node) {
            is TreeNode.DirectoryNode -> {
                // 目录节点的子节点渲染
                node.children.forEachIndexed { index, child ->
                    val isLast = index == node.children.lastIndex
                    val prefix = if (isLast) "└" else "├"

                    when (child) {
                        is TreeNode.FileNode -> {
                            val sizeInfo = child.size?.let { " ($it)" } ?: ""
                            output.appendLine("$indent$prefix── ${child.name}$sizeInfo")
                        }

                        is TreeNode.DirectoryNode -> {
                            output.appendLine("$indent$prefix── ${child.name}/")
                            renderTree(child, depth + 1, output)
                        }

                        is TreeNode.CompressedNode -> {
                            output.appendLine("$indent$prefix── {${child.subdirNames.joinToString(",")}}/")
                        }

                        is TreeNode.ParallelDirsNode -> {
                            // 以更紧凑的格式显示并列目录结构
                            val dirs = child.dirNames.sorted().joinToString(",")
                            output.appendLine("$indent$prefix── {$dirs}/${child.commonChildName}/")
                        }
                    }
                }
            }

            else -> {} // 其他类型节点在这里不需要单独处理
        }
    }

    /**
     * 判断目录是否应被排除
     */
    private fun isExcluded(project: Project, directory: PsiDirectory): Boolean {
        val excludedDirs = setOf(".idea", "build", "target", ".gradle", "node_modules")
        if (directory.name in excludedDirs) return true

        val status = FileStatusManager.getInstance(project).getStatus(directory.virtualFile)
        return status == FileStatus.IGNORED
    }
}
