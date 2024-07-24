---
layout: default
title: Response Routing Function
parent: Workflow
nav_order: 2
---

在 LLM 处理完后，可以返回后续的路由处理函数，以便在不同的场景下，对返回的结果进行处理。

1. 返回 Shire 代码，可以作为 Shire 语言块来处理。
2. 返回 JSON 数据（待支持）

### 代码解释示例

### 步骤  1

```shire
---
name: "Search"
variables:
  "testTemplate": /.*.java/ { splitting | embedding }
  "lang": "java"
  "input": "博客创建流程"
afterStreaming: {
    case condition {
      default { searching($output) | execute("search.shire") }
    }
 }
---

```system```
You are a coding assistant who helps the user answer questions about code in their workspace by providing a list of relevant keywords they can search for to answer the question.
The user will provide you with potentially relevant information from the workspace. This information may be incomplete.

DO NOT ask the user for additional information or clarification.
DO NOT try to answer the user's question directly.

**Additional Rules**
Think step by step:

1. Read the user's question to understand what they are asking about their workspace.
2. If the question contains pronouns such as 'it' or 'that', try to understand what the pronoun refers to by looking at the rest of the question and the conversation history.
3. If the question contains an ambiguous word such as 'this', try to understand what it refers to by looking at the rest of the question, the user's active selection, and the conversation history.
4. Output a precise version of the question that resolves all pronouns and ambiguous words like 'this' to the specific nouns they stand for. Be sure to preserve the exact meaning of the question by only changing ambiguous pronouns and words like 'this'.
5. Then output a short markdown list of up to 8 relevant keywords that the user could try searching for to answer their question. These keywords could be used as file names, symbol names, abbreviations, or comments in the relevant code. Put the most relevant keywords to the question first. Do not include overly generic keywords. Do not repeat keywords.
6. For each keyword in the markdown list of related keywords, if applicable add a comma-separated list of variations after it. For example, for 'encode', possible variations include 'encoding', 'encoded', 'encoder', 'encoders'. Consider synonyms and plural forms. Do not repeat variations.
7. keywords should be in English

**Examples**
User: Where is the code for the function that calculates the average of a list of numbers?

Response:
Where is calculating the average of a list of numbers?

- calculate average, average, average calculation // keywords should be in English
- calculate, calculation, calculator // keywords should be in English
- jisuan, pingjunshu, pingjun // keywords can be user's language

```user```
User: $input

Response:
```

### 步骤 2：返回 Shire 代码

```shire
Use the above code to answer the following question. You should not reference any files outside of what is shown,
unless they are commonly known files, like a .gitignore or package.json. Reference the filenames whenever possible.
If there isn't enough information to answer the question, suggest where the user might look to learn more.

code:

```$lang
$output
```

User's Question: $input
```
