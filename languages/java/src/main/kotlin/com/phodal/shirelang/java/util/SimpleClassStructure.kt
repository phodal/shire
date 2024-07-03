package com.phodal.shirelang.java.util

data class SimpleClassStructure(
    var fieldName: String,
    var fieldType: String,
    val children: List<SimpleClassStructure>,
    var builtIn: Boolean = false
) {
    private val childrenUml: MutableMap<String, String> = mutableMapOf()

    /**
     * Returns a PlantUML string representation of the class structure.
     *
     * This method generates a PlantUML string representation of the class structure based on the current object and its child objects. The resulting string is built using the buildPuml() method and the childPuml map. The resulting string will be a tree-like structure that shows the relationships between the classes.
     *
     * @return the PlantUML string representation of the class structure
     *
     * For example, if a BlogPost class includes a Comment class, and the Comment class includes a User class, then the resulting tree will be:
     *
     * ```puml
     * class BlogPost {
     *  id: long
     *  comment: Comment
     *}
     *
     * class Comment {
     *   user: User
     * }
     *
     * class User {
     *  name: String
     * }
     *```
     */
    override fun toString(): String {
        val diagramBuilder = StringBuilder()
        diagramBuilder.append(classStructureToUml(this))

        buildChildUmlHierarchy(children)

        childrenUml.forEach {
            diagramBuilder.append("\n")
            diagramBuilder.append(it.value)
        }

        return diagramBuilder.toString()
    }


    /**
     * Returns a PlantUML string representation of the class structure
     * for example:
     * ```
     * class BlogPost {
     *     long id;
     *     Comment comment;
     * }
     * class Comment {
     *     User user;
     * }
     * class User {
     *     String name;
     * }
     *```
     *
     * will be represented as:
     *
     * ```puml
     * class BlogPost {
     *    long id;
     *    Comment comment;
     *}
     *```
     */
    private fun classStructureToUml(simpleClassStructure: SimpleClassStructure): String {
        val children = simpleClassStructure.children.joinToString("\n") { "  ${it.fieldName}: ${it.fieldType}" }
        return "class ${simpleClassStructure.fieldType} {\n" +
                children +
                "\n}\n"
    }

    private fun buildChildUmlHierarchy(data: List<SimpleClassStructure>) {
        data.filter { !it.builtIn }.forEach {
            childrenUml[it.fieldType] = classStructureToUml(it)
            buildChildUmlHierarchy(it.children)
        }
    }

}