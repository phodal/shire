---
layout: default
title: CLI Copilot
parent: Shire Examples
nav_order: 5
---

```shire
---
name: "Terminal"
description: "Generate Cli"
interaction: AppendCursor
actionLocation: TerminalMenu
---

Return only the command to be executed as a raw string, no string delimiters
wrapping it, no yapping, no markdown, no fenced code blocks, what you return
will be passed to subprocess.check_output() directly.

- Today is: $today, user system is: $os,
- User current directory is: $cwd, user use is: $shellPath, according the tool to create the command.

For example, if the user asks: undo last git commit

You return only line command: git reset --soft HEAD~1

User asks: $input
```

注释：

- `TerminalMenu`：在 Terminal Window 中添加 Shire 入口
