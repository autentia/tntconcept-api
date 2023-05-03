package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createActivity
import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.core.domain.ActivitiesCalendarFactory
import com.autentia.tnt.binnacle.core.domain.CalendarFactory
import com.autentia.tnt.binnacle.entities.Organization
import com.autentia.tnt.binnacle.entities.Project
import com.autentia.tnt.binnacle.entities.ProjectRole
import com.autentia.tnt.binnacle.entities.RequireEvidence
import com.autentia.tnt.binnacle.entities.TimeUnit
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleUserDTO
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import com.autentia.tnt.binnacle.services.ActivityCalendarService
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.HolidayService
import io.micronaut.security.authentication.ClientAuthentication
import io.micronaut.security.utils.SecurityService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.*

internal class ProjectRoleByProjectIdUseCaseTest {

    private val activityService = mock<ActivityService>()
    private val holidayService = mock<HolidayService>()
    private val projectRoleRepository = mock<ProjectRoleRepository>()
    private val calendarFactory = CalendarFactory(holidayService)
    private val securityService = mock<SecurityService>()
    private val activityCalendarFactory = ActivitiesCalendarFactory(calendarFactory)
    private val activityCalendarService = ActivityCalendarService(calendarFactory, activityCalendarFactory)
    private val projectRoleResponseConverter = ProjectRoleResponseConverter()
    private val projectRoleByProjectIdUseCase =
        ProjectRoleByProjectIdUseCase(activityService, activityCalendarService, securityService, projectRoleRepository, projectRoleResponseConverter)

    @Test
    fun `return the expected project role`() {
        val projectRoles = listOf(
            ProjectRole(
                id = 1L,
                name = "Role ID 1",
                project = PROJECT,
                maxAllowed = 120,
                timeUnit = TimeUnit.MINUTES,
                requireEvidence = RequireEvidence.WEEKLY,
                isApprovalRequired = false,
                isWorkingTime = true
            ),
            ProjectRole(
                id = 2L,
                name = "Role ID 2",
                project = PROJECT,
                maxAllowed = 90,
                timeUnit = TimeUnit.MINUTES,
                requireEvidence = RequireEvidence.WEEKLY,
                isApprovalRequired = false,
                isWorkingTime = true
            ),
            ProjectRole(
                id = 3L,
                name = "Role ID 3",
                project = PROJECT,
                maxAllowed = 0,
                timeUnit = TimeUnit.MINUTES,
                requireEvidence = RequireEvidence.WEEKLY,
                isApprovalRequired = false,
                isWorkingTime = true
            ),
        )
        val activity = createActivity(projectRoles[0])
        val otherActivity = createActivity(projectRoles[1])

        val activitiesProjectRole1 = listOf(
            activity,
            activity.copy(start = activity.end, end = activity.end.plusMinutes(10), duration = 10)
        )
        val activitiesProjectRole2 = listOf(
            otherActivity
        )

        whenever(securityService.authentication).thenReturn(Optional.of(authentication))
        whenever(projectRoleRepository.getAllByProjectId(PROJECT_ID)).thenReturn(projectRoles)
        whenever(activityService.getProjectRoleActivities(1L)).thenReturn(activitiesProjectRole1)
        whenever(activityService.getProjectRoleActivities(2L)).thenReturn(activitiesProjectRole2)
        whenever(activityService.getProjectRoleActivities(3L)).thenReturn(emptyList())

        val expectedProjectRoles = listOf(
            buildProjectRoleUserDTO(1L, 120, 50),
            buildProjectRoleUserDTO(2L, 90, 30),
            buildProjectRoleUserDTO(3L, 0, 0),
        )

        assertEquals(expectedProjectRoles, projectRoleByProjectIdUseCase.get(PROJECT_ID))
    }

    private fun buildProjectRoleUserDTO(id: Long, maxAllowed: Int = 0, remaining: Int = 0): ProjectRoleUserDTO = ProjectRoleUserDTO(
        id = id,
        name = "Role ID $id",
        projectId = 1L,
        organizationId = 1L,
        maxAllowed = maxAllowed,
        remaining = remaining,
        timeUnit = TimeUnit.MINUTES,
        requireEvidence = RequireEvidence.WEEKLY,
        requireApproval = false,
        userId = USER_ID
    )

    private companion object {
        private const val USER_ID = 1L
        private const val PROJECT_ID = 1L

        private val ORGANIZATION = Organization(1L, "Nuestra empresa", listOf())
        private val PROJECT = Project(1L, "Dummy project", true, false, ORGANIZATION, listOf())

        private val PROJECT_ROLE =
            ProjectRole(PROJECT_ID, "Dummy Role", RequireEvidence.NO, PROJECT, 0, true, false, TimeUnit.MINUTES)

        private val authentication =
            ClientAuthentication(USER_ID.toString(), mapOf("roles" to listOf("admin")))
    }
}