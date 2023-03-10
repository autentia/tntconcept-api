package com.autentia.tnt.binnacle.converters

import com.autentia.tnt.binnacle.entities.Organization
import com.autentia.tnt.binnacle.entities.Project
import com.autentia.tnt.binnacle.entities.dto.ProjectResponseDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class ProjectResponseConverterTest {
    private lateinit var sut: ProjectResponseConverter

    @BeforeEach
    fun setUp() {
        sut = ProjectResponseConverter()
    }

    @Test
    fun `given entity Project should return ProjectResponseDTO with converted values`() {
        val project = Project(
            id = 1,
            name = "Dummy Project",
            open = false,
            billable = false,
            projectRoles = listOf(),
            organization = Mockito.mock(Organization::class.java)
        )

        val projectResponseDTO = sut.toProjectResponseDTO(project)

        assertEquals(project.id, projectResponseDTO.id)
        assertEquals(project.name, projectResponseDTO.name)
        assertEquals(project.open, projectResponseDTO.open)
        assertEquals(project.billable, projectResponseDTO.billable)
    }

    @Test
    fun `given Project list should return ProjectResponseDTO list with converted values`() {
        //Given
        val projectList = listOf(
            Project(
                id = 1,
                name = "First Project",
                open = false,
                billable = false,
                projectRoles = listOf(),
                organization = Mockito.mock(Organization::class.java)
            ),
            Project(
                id = 2,
                name = "Second Project",
                open = false,
                billable = true,
                projectRoles = listOf(),
                organization = Mockito.mock(Organization::class.java)
            ),
        )

        //When
        val projectResponseDTOList = projectList.map { sut.toProjectResponseDTO(it) }

        //Then
        val expectedProjectResponseDTOList = listOf(
            ProjectResponseDTO(
                id = 1,
                name = "First Project",
                open = false,
                billable = false,
            ),
            ProjectResponseDTO(
                id = 2,
                name = "Second Project",
                open = false,
                billable = true,
            ),
        )

        assertEquals(expectedProjectResponseDTOList, projectResponseDTOList)
    }
}
