---
layout: default
title: Code Comments
parent: Shire Examples
nav_order: 4
---

示例：生成注释

    ---
    name: "生成注释"
    interaction: InsertBeforeSelection
    actionLocation: ContextMenu
    onStreamingEnd: { insertNewline | formatCode }
    ---
    
    为如下的代码编写注释，使用 javadoc 风格：
    
    ```$language
    $selection
    ```
    
    只返回注释

解释：

- `InsertBeforeSelection`：在选中的代码之前插入注释
- `insertNewLine`：在插入注释后换行
- `formatCode`：格式化代码