package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.core.domain.ProjectRolesRecent
import com.autentia.tnt.binnacle.core.domain.StartEndLocalDateTime
import com.autentia.tnt.binnacle.core.domain.TimeInterval
import com.autentia.tnt.binnacle.entities.Activity
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleUserDTO
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import com.autentia.tnt.binnacle.services.ActivityCalendarService
import com.autentia.tnt.binnacle.services.ActivityService
import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.LocalTime
import javax.transaction.Transactional

@Singleton
class LatestProjectRolesForAuthenticatedUserUseCase internal constructor(
    private val projectRoleRepository: ProjectRoleRepository,
    private val projectRoleResponseConverter: ProjectRoleResponseConverter,
    private val activityService: ActivityService,
    private val activityCalendarService: ActivityCalendarService
) {
    @Transactional
    @ReadOnly
    fun get(): List<ProjectRoleUserDTO> {
        val oneMonthDateRange = oneMonthTimeIntervalFromCurrentDate()
        val dateRange = dateRangeOfCurrentYear()
        val currentYearTimeInterval = TimeInterval.of(dateRange.startDate, dateRange.endDate)

        val activities =
            activityService.getActivitiesOfLatestProjects(currentYearTimeInterval)

        val remainingGroupedByProjectRoleAndUserId = activityCalendarService.getRemainingGroupedByProjectRoleAndUser(
            activities.map(Activity::toDomain), currentYearTimeInterval.getDateInterval(),
            oneMonthDateRange
        )

        return remainingGroupedByProjectRoleAndUserId.map(projectRoleResponseConverter::toProjectRoleUserDTO)
    }

    @Deprecated("Use get method instead")
    @Transactional
    @ReadOnly
    fun getProjectRolesRecent(): List<ProjectRolesRecent> {
        val oneMonthDateRange = oneMonthDateRangeFromCurrentDate()

        val roles = projectRoleRepository.findDistinctProjectRolesBetweenDate(
            oneMonthDateRange.startDate,
            oneMonthDateRange.endDate
        )

        return roles
            .filter { it.projectOpen }
            .sortedByDescending { it.date }
            .distinctBy { it.id }
    }

    private fun oneMonthDateRangeFromCurrentDate(): StartEndLocalDateTime {
        val now = LocalDate.now()
        val startDate = now.minusMonths(1).atTime(LocalTime.MIN)
        val endDate = now.atTime(23, 59, 59)

        return StartEndLocalDateTime(startDate, endDate)
    }

    private fun oneMonthTimeIntervalFromCurrentDate(): TimeInterval {
        val now = LocalDate.now()
        val startDate = now.minusMonths(1).atTime(LocalTime.MIN)
        val endDate = now.atTime(23, 59, 59)

        return TimeInterval.of(startDate, endDate)
    }

    private fun dateRangeOfCurrentYear(): StartEndLocalDateTime {
        val now = LocalDate.now()
        val startDate = LocalDate.of(now.year, 1, 1).atTime(LocalTime.MIN)
        val endDate = now.atTime(23, 59, 59)

        return StartEndLocalDateTime(startDate, endDate)
    }
}