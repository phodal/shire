package com.phodal.shirecore.codemodel.base

import com.intellij.psi.PsiElement

/**
 * The `FormatableElement` is an abstract class that represents a formatable element in a given context.
 * It provides information about the root element, text, and name of the element.
 *
 * @property root The root element of the context.
 * @property text The text representation of the context.
 * @property name The name of the element in the context.
 *
 * This class has a method `format()` which formats the named element context into a string representation.
 * The format of the string representation varies depending on the context.
 * For instance:
 * In the context of `com.phodal.shirecore.codemodel.DirectoryStructure`, the formatted string representation will be like a directory structure.
 * In the context of `com.phodal.shirecore.codemodel.FileStructure`, the formatted string representation will be like a file structure.
 * In the context of `com.phodal.shirecore.codemodel.ClassStructure`, the formatted string representation will be like UML.
 * In the context of `com.phodal.shirecore.codemodel.MethodStructure`, the formatted string representation will be just the method signature.
 * In the context of `com.phodal.shirecore.codemodel.VariableStructure`, the formatted string representation will be like a variable declaration.
 *
 * @return The formatted string representation of the named element context.
 */
abstract class FormatableElement(open val root: PsiElement, open val text: String?, open val name: String?) {
    /**
     * Formats the named element context into a string representation.
     * In [com.phodal.shirecore.codemodel.ClassStructure], the formatted string representation will be like UML.For example:
     * ```uml
     * 'package: cc.unitmesh.untitled.demo.controller.UserController
     * '@RestController, @RequestMapping("/user")
     * class UserController {
     *   + @GetMapping     public UserDTO getUsers()
     * }
     * ```
     * In [com.phodal.shirecore.codemodel.MethodStructure],
     * the formatted string representation will be just the method signature.For Example
     * ```bash
     * path: /src/test.go
     * language: Go
     * fun name: f3
     * fun signature: (float64, float64, float64)
     * ```
     *
     * In [com.phodal.shirecore.codemodel.VariableStructure], the formatted string representation will be like:
     * ```bash
     * var name: content
     * var method name: format
     * var class name: NamedElementContext
     * ```
     * @return The formatted string representation of the named element context.
     */
    open fun format(): String = ""
}
