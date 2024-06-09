---
layout: default
title: Pattern Action
parent: Shire Language
nav_order: 4
---

好的，以下是一个通用的定义，适用于各种脚本和编程语言：

---

### Pattern-Action 定义

**Pattern-action** 是一种编程结构，用于根据特定条件（模式）执行相应操作的机制。它通常由两个部分组成：

1. **Pattern（模式）**：匹配输入数据的规则或条件。模式可以是正则表达式、特定值、范围或逻辑表达式，用于确定哪些输入数据满足条件。
2. **Action（动作）**：当输入数据匹配模式时要执行的操作。动作是由一组命令或代码组成，定义了对匹配数据的处理方式。

### 格式
```plaintext
pattern { action }
```

- **pattern**：用于匹配输入数据的条件。
- **action**：对匹配数据执行的操作。

### Shire 示例

```
/**.java/ { print $0 }  # 匹配所有 Java 文件并打印
```

### 其它语言示例

```textproc
# 模式-动作对
match /pattern/ do
  print $0
end

# 模式-替换
replace /foo/ with /bar/

# 模式-执行
match /pattern/ do
  exec "echo 'pattern found'"
end

# 模式-分支
case $1 in
  "start")
    print "Starting service"
  ;;
  "stop")
    print "Stopping service"
  ;;
  "restart")
    print "Restarting service"
  ;;
  *)
    print "Usage: {start|stop|restart}"
  ;;
end

# 变量和数组
let count = 0
match /pattern/ do
  let count = count + 1
end
print "Total patterns found: " + count

# 正则表达式匹配
match /[0-9]+/ do
  print "Number found: " + $0
end
```

以下是通用的示例，适用于不同脚本和编程语言：

- **AWK 脚本**：

```awk

/error/ { print $0 }  # 匹配包含 "error" 的行并打印
$1 > 100 { print $0 } # 匹配第一字段大于 100 的行并打印
```

- **Python**：

```python

data = ["error in line 1", "all good", "value 150", "value 50"]
for line in data:
    if "error" in line:
        print(line)  # 匹配包含 "error" 的行并打印
    if int(line.split()[-1]) > 100:
        print(line)  # 匹配最后一个数值大于 100 的行并打印
```

- **Shell 脚本**：

```sh
while read line; do
    if [[ "$line" == *"error"* ]]; then
        echo "$line"  # 匹配包含 "error" 的行并打印
    fi
    if [[ $(echo $line | awk '{print $NF}') -gt 100 ]]; then
        echo "$line"  # 匹配最后一个字段大于 100 的行并打印
    fi
done < inputfile
```

在这些示例中：

- **Pattern（模式）**：是用于匹配输入数据的条件，如包含特定字符串或满足某个数值条件。
- **Action（动作）**：是在模式匹配成功时执行的操作，如打印匹配的行。

这种 `pattern-action` 结构在数据处理、文本分析、日志筛选等场景中广泛应用，提供了一种简洁而强大的方法来处理和分析数据。