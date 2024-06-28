package com.phodal.shirelang.java.toolchain

import com.intellij.openapi.project.Project
import com.phodal.shirecore.toolset.TechStack
import com.phodal.shirelang.java.archmeta.LibraryDescriptor
import com.phodal.shirelang.java.archmeta.SpringLibrary

object TechStackConverter {
    fun convertTechStack(project: Project): TechStack {
        val libraryDataList =
            GradleBuildTool().prepareLibraryData(project) ?: MavenBuildTool().prepareLibraryData(project)

        val techStack = TechStack()
        var hasMatchSpringMvc = false
        var hasMatchSpringData = false

        libraryDataList.forEach {
            val name = it.groupId + ":" + it.artifactId
            if (!hasMatchSpringMvc) {
                SpringLibrary.SPRING_MVC.forEach { entry: LibraryDescriptor ->
                    if (name.contains(entry.coords)) {
                        techStack.coreFrameworks.putIfAbsent(entry.shortText, true)
                        hasMatchSpringMvc = true
                    }
                }
            }

            if (!hasMatchSpringData) {
                SpringLibrary.SPRING_DATA.forEach { entry ->
                    entry.coords.forEach { coord ->
                        if (name.contains(coord)) {
                            techStack.coreFrameworks.putIfAbsent(entry.shortText, true)
                            hasMatchSpringData = true
                        }
                    }
                }
            }

            when {
                name.contains("org.springframework.boot:spring-boot-test") -> {
                    techStack.testFrameworks.putIfAbsent("Spring Boot Test", true)
                }

                name.contains("org.assertj:assertj-core") -> {
                    techStack.testFrameworks.putIfAbsent("AssertJ", true)
                }

                name.contains("org.junit.jupiter:junit-jupiter") -> {
                    techStack.testFrameworks.putIfAbsent("JUnit 5", true)
                }

                name.contains("org.mockito:mockito-core") -> {
                    techStack.testFrameworks.putIfAbsent("Mockito", true)
                }

                name.contains("com.h2database:h2") -> {
                    techStack.testFrameworks.putIfAbsent("H2", true)
                }
            }
        }

        return techStack
    }
}