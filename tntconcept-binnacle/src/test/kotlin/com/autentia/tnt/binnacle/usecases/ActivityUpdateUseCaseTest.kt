package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createProjectRole
import com.autentia.tnt.binnacle.config.createUser
import com.autentia.tnt.binnacle.converters.ActivityIntervalResponseConverter
import com.autentia.tnt.binnacle.converters.ActivityRequestBodyConverter
import com.autentia.tnt.binnacle.converters.ActivityResponseConverter
import com.autentia.tnt.binnacle.converters.TimeIntervalConverter
import com.autentia.tnt.binnacle.core.domain.ActivityRequestBody
import com.autentia.tnt.binnacle.entities.Activity
import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.Organization
import com.autentia.tnt.binnacle.entities.Project
import com.autentia.tnt.binnacle.entities.ProjectRole
import com.autentia.tnt.binnacle.entities.RequireEvidence
import com.autentia.tnt.binnacle.entities.TimeUnit
import com.autentia.tnt.binnacle.entities.dto.ActivityRequestBodyDTO
import com.autentia.tnt.binnacle.entities.dto.ActivityResponseDTO
import com.autentia.tnt.binnacle.entities.dto.IntervalResponseDTO
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleUserDTO
import com.autentia.tnt.binnacle.services.ActivityCalendarService
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.ProjectRoleService
import com.autentia.tnt.binnacle.services.UserService
import com.autentia.tnt.binnacle.validators.ActivityValidator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

internal class ActivityUpdateUseCaseTest {

    private val activityService = mock<ActivityService>()
    private val activityCalendarService = mock<ActivityCalendarService>()
    private val activityValidator = mock<ActivityValidator>()
    private val projectRoleService = mock<ProjectRoleService>()
    private val userService = mock<UserService>()

    private val activityUpdateUseCase = ActivityUpdateUseCase(
        activityService,
        activityCalendarService,
        projectRoleService,
        userService,
        activityValidator,
        ActivityRequestBodyConverter(),
        ActivityResponseConverter(
            ActivityIntervalResponseConverter()
        ),
        TimeIntervalConverter()
    )

    private val projectRole = createProjectRole().copy(id = 10L)

    @Test
    fun `return updated activity for the authenticated user when it is valid`() {
        doReturn(USER).whenever(userService).getAuthenticatedUser()

        doReturn(projectRole).whenever(projectRoleService).getByProjectRoleId(projectRole.id)

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

        private val PROJECT = Project(
            1L,
            "Dummy Project",
            open = true,
            billable = false,
            projectRoles = listOf(),
            organization = ORGANIZATION
        )
        private val PROJECT_ROLE = ProjectRole(10L, "Dummy Project role", RequireEvidence.NO, PROJECT, 0, true, false, TimeUnit.MINUTES)
        private val PROJECT_ROLE_RESPONSE_DTO = ProjectRoleUserDTO(10L, "Dummy Project role", PROJECT_ROLE.project.organization.id, PROJECT_ROLE.project.id, PROJECT_ROLE.maxAllowed, PROJECT_ROLE.maxAllowed, PROJECT_ROLE.timeUnit, PROJECT_ROLE.requireEvidence, PROJECT_ROLE.isApprovalRequired, USER.id )
        private val NEW_ACTIVITY_DTO = ActivityRequestBodyDTO(
            1L,
            TODAY,
            TODAY.plusMinutes(75L),
            "New activity",
            false,
            PROJECT_ROLE.id,
            false,
        )
        private val todayActivity = Activity(
            1,
            TODAY,
            TODAY.plusMinutes(75L),
            75,
            "New activity",
            PROJECT_ROLE,
            USER.id,
            false,
            null,
            null,
            false,
            approvalState = ApprovalState.NA
        )
        private val todayActivityResponseDTO = ActivityResponseDTO(
            false,
            "New activity",
            false,
            1,
            PROJECT_ROLE_RESPONSE_DTO.id,
            IntervalResponseDTO(TODAY, TODAY.plusMinutes(75L), 75, PROJECT_ROLE.timeUnit),
            USER.id,
            approvalState = ApprovalState.NA
        )
        private val activityToUpdate = ActivityRequestBody(
            1L,
            TODAY,
            TODAY.plusMinutes(75L),
            75,
            "New activity",
            false,
            PROJECT_ROLE.id,
            false
        )
    }
}

