package com.autentia.tnt.binnacle.services

import com.autentia.tnt.binnacle.entities.ProjectRole
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class ProjectRoleServiceTest {

    private val projectRoleRepository = mock<ProjectRoleRepository>()

    private val projectRoleService = ProjectRoleService(projectRoleRepository)

    @Test
    fun `return the expected project roles`() {
        val projectRoles = listOf(mock<ProjectRole>())
        val ids = listOf(1, 2)

        doReturn(projectRoles).whenever(projectRoleRepository).getAllByIdIn(ids.map(Int::toLong))

        val actual = projectRoleService.getAllByIds(ids)

        assertEquals(projectRoles, actual)
    }

}
