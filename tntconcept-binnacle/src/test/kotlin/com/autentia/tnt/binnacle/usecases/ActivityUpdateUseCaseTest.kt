package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createUser
import com.autentia.tnt.binnacle.converters.ActivityRequestBodyConverter
import com.autentia.tnt.binnacle.converters.ActivityResponseConverter
import com.autentia.tnt.binnacle.converters.OrganizationResponseConverter
import com.autentia.tnt.binnacle.converters.ProjectResponseConverter
import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.core.domain.ActivityRequestBody
import com.autentia.tnt.binnacle.entities.Activity
import com.autentia.tnt.binnacle.entities.Organization
import com.autentia.tnt.binnacle.entities.Project
import com.autentia.tnt.binnacle.entities.ProjectRole
import com.autentia.tnt.binnacle.entities.dto.ActivityRequestBodyDTO
import com.autentia.tnt.binnacle.entities.dto.ActivityResponseDTO
import com.autentia.tnt.binnacle.entities.dto.OrganizationResponseDTO
import com.autentia.tnt.binnacle.entities.dto.ProjectResponseDTO
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleResponseDTO
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.UserService
import com.autentia.tnt.binnacle.validators.ActivityValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever

internal class ActivityUpdateUseCaseTest {

    private val activityService = mock<ActivityService>()
    private val activityValidator = mock<ActivityValidator>()
    private val userService = mock<UserService>()

    private val activityUpdateUseCase = ActivityUpdateUseCase(
        activityService,
        userService,
        activityValidator,
        ActivityRequestBodyConverter(),
        ActivityResponseConverter(
            OrganizationResponseConverter(),
            ProjectResponseConverter(),
            ProjectRoleResponseConverter()
        )
    )

    @Test
    fun `return updated activity for the authenticated user when it is valid`() {
        doReturn(USER).whenever(userService).getAuthenticatedUser()

        doReturn(todayActivity).whenever(activityService).updateActivity(any(), eq(USER))

        assertEquals(todayActivityResponseDTO, activityUpdateUseCase.updateActivity(NEW_ACTIVITY_DTO))
    }

    @Test
    fun `rethrow any exception from the validator`() {
        doReturn(USER).whenever(userService).getAuthenticatedUser()

        doAnswer { throw Exception() }.whenever(activityValidator).checkActivityIsValidForUpdate(activityToUpdate, USER)

        assertThrows<Exception> { activityUpdateUseCase.updateActivity(NEW_ACTIVITY_DTO) }
    }

    private companion object {
        private val USER = createUser()
        private val TODAY = LocalDateTime.now()
        private val ORGANIZATION = Organization(1L, "Dummy Organization", listOf())
        private val ORGANIZATION_DTO = OrganizationResponseDTO(1L, "Dummy Organization")

        private val PROJECT = Project(
            1L,
            "Dummy Project",
            open = true,
            billable = false,
            projectRoles = listOf(),
            organization = ORGANIZATION
        )
        private val PROJECT_RESPONSE_DTO = ProjectResponseDTO(
            1L,
            "Dummy Project",
            open = true,
            billable = false
        )
        private val PROJECT_ROLE = ProjectRole(10L, "Dummy Project role", false, PROJECT, 0)
        private val PROJECT_ROLE_RESPONSE_DTO = ProjectRoleResponseDTO(10L, "Dummy Project role", false)
        private val NEW_ACTIVITY_DTO = ActivityRequestBodyDTO(
            1L,
            TODAY,
            75,
            "New activity",
            false,
            PROJECT_ROLE.id,
            false
        )
        private val todayActivity = Activity(
            1,
            TODAY,
            75,
            "New activity",
            PROJECT_ROLE,
            USER.id,
            false,
            null,
            null,
            false,
        )
        private val todayActivityResponseDTO = ActivityResponseDTO(
            1,
            TODAY,
            75,
            "New activity",
            PROJECT_ROLE_RESPONSE_DTO,
            USER.id,
            false,
            ORGANIZATION_DTO,
            PROJECT_RESPONSE_DTO,
            false,
        )
        private val activityToUpdate = ActivityRequestBody(
            1L,
            TODAY,
            75,
            "New activity",
            false,
            PROJECT_ROLE.id,
            false
        )
    }
}

