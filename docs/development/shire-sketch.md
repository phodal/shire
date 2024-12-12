---
layout: default
title: Shire Sketch
parent: Development
nav_order: 3
---

Shire Sketch 是 Shire 提供的 IDE 画布功能，旨在通过其丰富的文本代码（源码、Patch、UML、架构图等）二次处理、渲染组件，进一步简化交互成本，
以提升开发者在 IDE 中的体验。
无论是单个文件的显示、渲染操作，还是多文件协作、修复，Shire Sketch 都能提供强大的支持。

- **实时流式代码高亮**：实时显示代码高亮的流式视图。
- **内置差异（Patch 语言）**：显示代码差异的内置视图。
- **实时流式差异（StreamDiff）**：实时显示代码差异的流式视图，基于 Continue 的 UI 修改。
- **Mermaid 流程图**：支持 Mermaid 流程图的渲染，与双向绑定的代码编辑器。（要求启用 Mermaid 插件）
- **PlantUML 图表**：支持 PlantUML 图表的渲染，与双向绑定的代码编辑器。（要求安装 `PlantUML integration` 插件）

## 创建新 Sketch

```kotlin
interface LanguageSketchProvider {
    fun isSupported(lang: String): Boolean

    fun create(project: Project, content: String): ExtensionLangSketch

    companion object {
        private val EP_NAME: ExtensionPointName<LanguageSketchProvider> =
            ExtensionPointName("com.phodal.shireLangSketchProvider")

        fun provide(language: String): LanguageSketchProvider? {
            return EP_NAME.extensionList.firstOrNull {
                it.isSupported(language)
            }
        }
    }
}
```

示例：

XML 声明：

```xml

<extensions defaultExtensionNs="com.phodal">
    <shireLangSketchProvider implementation="com.phodal.shirecore.sketch.patch.DiffLangSketchProvider"/>
</extensions>
```

实现代码：

```kotlin
class DiffLangSketchProvider : LanguageSketchProvider {
    override fun isSupported(lang: String): Boolean = lang == "diff" || lang == "patch"
    override fun create(project: Project, content: String): ExtensionLangSketch = DiffLangSketch(project, content)
}
```