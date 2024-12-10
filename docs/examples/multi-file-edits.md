# Multi-File Edits Feature

## Overview

The multi-file edits feature allows users to perform edits across multiple files in the repository. This feature includes code menus with options like Inlay Diff, Apply, Revert, and Cancel. It uses `TwosideContentPanel` and `SimpleDiffViewer` for the UI component. Additionally, it includes error handling and a feature for navigating to symbols using a `/goto` command. The compile output includes a hyperlink that auto-parses. The feature also integrates multiple file editing with a chat panel.

## Usage Instructions

### Code Menus

The code menus provide the following options:

- **Inlay Diff**: Shows the differences between the current code and the changes.
- **Apply**: Applies the changes to the code.
- **Revert**: Reverts the changes to the original code.
- **Cancel**: Cancels the current operation.

### Navigating to Symbols

To navigate to a symbol, use the `/goto` command followed by the symbol name. For example:

```
/goto MyClass
```

### Compile Output

The compile output includes hyperlinks that auto-parse. For example:

```
Compiling...
See details at: https://example.com/compile-output
```

### Chat Panel Integration

The multi-file editing feature is integrated with a chat panel, allowing users to interact with the edits in real-time.

## Examples

### Example 1: Inlay Diff

1. Open the code menu and select "Inlay Diff".
2. The `TwosideContentPanel` and `SimpleDiffViewer` will display the differences between the current code and the changes.

### Example 2: Apply Changes

1. Open the code menu and select "Apply".
2. The changes will be applied to the code.

### Example 3: Revert Changes

1. Open the code menu and select "Revert".
2. The changes will be reverted to the original code.

### Example 4: Cancel Operation

1. Open the code menu and select "Cancel".
2. The current operation will be canceled.

### Example 5: Navigate to Symbol

1. Use the `/goto` command followed by the symbol name.
2. The editor will navigate to the specified symbol.

### Example 6: Compile Output with Hyperlink

1. Compile the code.
2. The compile output will include a hyperlink that auto-parses.

### Example 7: Chat Panel Integration

1. Open the chat panel.
2. Interact with the multi-file edits in real-time.

