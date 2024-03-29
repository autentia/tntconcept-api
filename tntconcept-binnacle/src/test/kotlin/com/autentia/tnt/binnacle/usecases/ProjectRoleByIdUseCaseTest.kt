package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createProjectRole
import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.entities.dto.MaxTimeAllowedDTO
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleDTO
import com.autentia.tnt.binnacle.entities.dto.TimeInfoDTO
import com.autentia.tnt.binnacle.exception.ProjectRoleNotFoundException
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class ProjectRoleByIdUseCaseTest {

    private val id = 1L
    private val projectRoleRepository = mock<ProjectRoleRepository>()
    private val projectRoleByIdUseCase =
        ProjectRoleByIdUseCase(projectRoleRepository, ProjectRoleResponseConverter())

    @Test
    fun `find project role by id`() {
        val projectRole = createProjectRole()
        whenever(projectRoleRepository.findById(id)).thenReturn(projectRole)

        assertEquals(
            ProjectRoleDTO(
                projectRole.id,
                projectRole.name,
                projectRole.project.organization.id,
                projectRole.project.id,
                TimeInfoDTO(MaxTimeAllowedDTO(projectRole.maxTimeAllowedByYear, projectRole.maxTimeAllowedByActivity), projectRole.timeUnit),
                projectRole.isWorkingTime,
                projectRole.requireEvidence,
                projectRole.isApprovalRequired
            ), projectRoleByIdUseCase.get(id)
        )
    }

    @Test
    fun `find project role by id should throw exception`(){
        whenever(projectRoleRepository.findById(id)).thenReturn(null)

        assertThrows<ProjectRoleNotFoundException> { projectRoleByIdUseCase.get(id) }

    }
}
