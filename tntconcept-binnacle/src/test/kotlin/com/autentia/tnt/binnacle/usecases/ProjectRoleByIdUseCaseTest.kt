package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createProjectRole
import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleDTO
import com.autentia.tnt.binnacle.exception.ProjectRoleNotFoundException
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import com.autentia.tnt.binnacle.services.ActivityService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class ProjectRoleByIdUseCaseTest {

    private val id = 1L
    private val projectRoleRepository = mock<ProjectRoleRepository>()
    private val activityService = mock<ActivityService>()
    private val projectRoleByIdUseCase =
        ProjectRoleByIdUseCase(projectRoleRepository, ProjectRoleResponseConverter(activityService))

    @Test
    fun `find project role by id`() {
        val projectRole = createProjectRole()
        whenever(projectRoleRepository.findById(id)).thenReturn(projectRole)
        whenever(projectRoleRepository.findById(id)).thenReturn(projectRole)

        assertEquals(
            ProjectRoleDTO(
                projectRole.id,
                projectRole.name,
                projectRole.project.organization.id,
                projectRole.project.id,
                projectRole.maxAllowed,
                projectRole.timeUnit,
                projectRole.requireEvidence,
                projectRole.isApprovalRequired
            ), projectRoleByIdUseCase.get(id)
        )
    }

    @Test
    fun `throw role was not found exception`() {
        whenever(projectRoleRepository.findById(id)).thenReturn(null)

        assertThrows<ProjectRoleNotFoundException> { projectRoleByIdUseCase.get(id) }
    }
}
