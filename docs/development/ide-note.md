---
layout: default
title: IDE Note
nav_order: 999
parent: Development
---

## CoroutineScope issue

示例：

```kotlin
ShireCoroutineScope.scope(context.project).launch {
    val suggestion = StringBuilder()

    flow?.cancelWithConsole(context.console)?.cancellable()?.collect { char ->
        suggestion.append(char)

        invokeLater {
            context.console?.print(char, ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

    postExecute.invoke(suggestion.toString(), null)
}
```

## Data Context

### 1. 从 AnAction 中获取 DataContext

如果你正在实现一个继承自 `AnAction` 的动作类，可以通过 `AnActionEvent` 直接获取 `DataContext`。如下所示：

```java
public class MyAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        // 你可以从 DataContext 获取更多数据
    }
}
```

### 2. 从一个组件中获取 DataContext

如果你有一个 UI 组件，比如一个按钮，你可以使用 `DataManager` 来获取该组件的 `DataContext`。例如：

```java
JComponent component = ...; // 你的组件
DataContext dataContext = DataManager.getInstance().getDataContext(component);
```

### 3. 从当前焦点的组件获取 DataContext

如果你需要从当前焦点的组件获取 `DataContext`，可以这样做：

```java
DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
```

### 4. 使用 PlatformDataKeys

获取 `DataContext` 后，你可以使用 `PlatformDataKeys` 来提取特定的数据。例如，获取当前的 `Editor`：

```java
Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
Project project = CommonDataKeys.PROJECT.getData(dataContext);
VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(dataContext);
```

### 5. 存储 Event 的 DataContext

在某些情况下，你可能需要在动作执行之前存储 `DataContext`。你可以使用 `VariableActionEventDataHolder` 来存储数据。例如：

```kotlin
class ShireSonarLintAction : AnAction() {
    // ...
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        VariableActionEventDataHolder.putData(VariableActionEventDataHolder(e.dataContext))

        val config = shireActionConfigs(project).firstOrNull() ?: return
        ShireRunFileAction.executeShireFile(project, config, null)
    }
}
```

使用 `VariableActionEventDataHolder` 存储 `DataContext` 后，你可以在动作执行时获取数据。例如：

```kotlin
fun getCommitWorkflowUi(): CommitWorkflowUi? {
    VariableActionEventDataHolder.getData()?.vcsDataContext?.let {
        val commitWorkflowUi = it.getData(VcsDataKeys.COMMIT_WORKFLOW_UI)
        return commitWorkflowUi as CommitWorkflowUi?
    }

    val dataContext = DataManager.getInstance().dataContextFromFocus.result
    val commitWorkflowUi = dataContext?.getData(VcsDataKeys.COMMIT_WORKFLOW_UI)
    return commitWorkflowUi
}
```


## 自定义 ContextMenu 位置

参考：`ShireActionStartupActivity` 中的实现，如：`attachTerminalAction` 方法

```kotlin
private fun attachTerminalAction() {
    val actionManager = ActionManager.getInstance()
    val toolsMenu = actionManager.getAction("TerminalToolwindowActionGroup") as? DefaultActionGroup ?: return

    val action = actionManager.getAction("ShireTerminalAction")
    if (!toolsMenu.containsAction(action)) {
        toolsMenu.add(action)
    }
}
```

1. 从 ActionManager 中获取目标 ActionGroup
2. 将自定义 Action 添加到目标 ActionGroup 中
