package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.OrganizationResponseConverter
import com.autentia.tnt.binnacle.entities.Organization
import com.autentia.tnt.binnacle.entities.Project
import com.autentia.tnt.binnacle.entities.ProjectRole
import com.autentia.tnt.binnacle.entities.dto.OrganizationResponseDTO
import com.autentia.tnt.binnacle.repositories.OrganizationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class ImputableOrganizationsUseCaseTest {

    private val organizationRepository = mock<OrganizationRepository>()

    private val imputableOrganizationsUseCase =
        ImputableOrganizationsUseCase(organizationRepository, OrganizationResponseConverter())

    @Test
    fun `return all imputable organizations`() {

        doReturn(ORGANIZATIONS).whenever(organizationRepository).findAll()

        assertEquals(
            listOf(OrganizationResponseDTO(id = 1, name = "Open/Closed projects")),
            imputableOrganizationsUseCase.get()
        )
    }

    private companion object {

        private fun buildProjectRole(id: Long): ProjectRole {
            return ProjectRole(id, "Project Role ID $id", false, mock(Project::class.java), 0)
        }

        private fun buildProject(id: Long, open: Boolean, roles: List<ProjectRole>): Project {
            return Project(id, "Project ID $id", open, false, mock(Organization::class.java), roles)
        }

        private val OPEN_PROJECT = buildProject(1, true, listOf(buildProjectRole(id = 1)))
        private val CLOSED_PROJECT = buildProject(2, false, listOf(buildProjectRole(id = 2)))

        private val ORGANIZATIONS = listOf(
            Organization(1, "Open/Closed projects", listOf(OPEN_PROJECT, CLOSED_PROJECT)),
            Organization(2, "Open Project but without roles", listOf(buildProject(3, true, listOf()))),
            Organization(3, "Closed Project", listOf(buildProject(4, false, listOf(buildProjectRole(4)))))
        )

    }

}
