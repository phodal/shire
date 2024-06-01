package com.phodal.shirelang.completion.dataprovider

enum class BuiltinRefactorCommand(val funcName: String, val description: String) {
    RENAME("rename", "Rename a file"),
    SAFE_DELETE("safe-delete", "Safe delete a file"),
    DELETE("delete", "Delete a file"),
    MOVE("move", "Move a file"),
    ;

    companion object {
        fun all(): List<BuiltinRefactorCommand> {
            return values().toList()
        }

        fun fromString(command: String): BuiltinRefactorCommand? {
            return values().find { it.name.equals(command, ignoreCase = true) }
        }
    }
}