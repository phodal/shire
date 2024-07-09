---
layout: default
title: IDE Note
nav_order: 999
parent: Development
---

## CoroutineScope issue

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
