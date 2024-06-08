package com.phodal.shirelang.java.toolchain

import com.intellij.openapi.externalSystem.model.project.LibraryData
import com.intellij.openapi.externalSystem.service.project.ProjectDataManager
import com.intellij.openapi.project.Project
import com.phodal.shirecore.toolset.TestStack
import org.jetbrains.plugins.gradle.util.GradleConstants

object GradleLibraryConverter {
    fun convertTechStack(project: Project): TestStack {
        val libraryDataList = prepareLibraryData(project)

        val testStack = TestStack()
        var hasMatchSpringMvc = false
        var hasMatchSpringData = false

        libraryDataList?.forEach {
            val name = it.groupId + ":" + it.artifactId
            if (!hasMatchSpringMvc) {
                SpringLibrary.SPRING_MVC.forEach { entry: LibraryDescriptor ->
                    if (name.contains(entry.coords)) {
                        testStack.coreFrameworks.putIfAbsent(entry.shortText, true)
                        hasMatchSpringMvc = true
                    }
                }
            }

            if (!hasMatchSpringData) {
                SpringLibrary.SPRING_DATA.forEach { entry ->
                    entry.coords.forEach { coord ->
                        if (name.contains(coord)) {
                            testStack.coreFrameworks.putIfAbsent(entry.shortText, true)
                            hasMatchSpringData = true
                        }
                    }
                }
            }

            when {
                name.contains("org.springframework.boot:spring-boot-test") -> {
                    testStack.testFrameworks.putIfAbsent("Spring Boot Test", true)
                }

                name.contains("org.assertj:assertj-core") -> {
                    testStack.testFrameworks.putIfAbsent("AssertJ", true)
                }

                name.contains("org.junit.jupiter:junit-jupiter") -> {
                    testStack.testFrameworks.putIfAbsent("JUnit 5", true)
                }

                name.contains("org.mockito:mockito-core") -> {
                    testStack.testFrameworks.putIfAbsent("Mockito", true)
                }

                name.contains("com.h2database:h2") -> {
                    testStack.testFrameworks.putIfAbsent("H2", true)
                }
            }
        }

        return testStack
    }

    fun prepareLibraryData(project: Project): List<LibraryData>? {
        val basePath = project.basePath ?: return null
        val projectData = ProjectDataManager.getInstance().getExternalProjectData(
            project, GradleConstants.SYSTEM_ID, basePath
        )

        val libraryDataList: List<LibraryData>? = projectData?.externalProjectStructure?.children?.filter {
            it.data is LibraryData
        }?.map {
            it.data as LibraryData
        }

        return libraryDataList
    }
}