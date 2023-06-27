package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.ProjectRoleConverter
import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.core.domain.TimeInterval
import com.autentia.tnt.binnacle.entities.Activity
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleUserDTO
import com.autentia.tnt.binnacle.repositories.ActivityRepository
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import com.autentia.tnt.binnacle.services.ActivityCalendarService
import com.autentia.tnt.security.application.checkAuthentication
import com.autentia.tnt.security.application.id
import io.micronaut.security.utils.SecurityService
import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.LocalTime
import javax.transaction.Transactional

@Singleton
class LatestProjectRolesForAuthenticatedUserUseCase internal constructor(
    private val projectRoleRepository: ProjectRoleRepository,
    private val projectRoleResponseConverter: ProjectRoleResponseConverter,
    private val activityRepository: ActivityRepository,
    private val activityCalendarService: ActivityCalendarService,
    private val securityService: SecurityService,
    private val projectRoleConverter: ProjectRoleConverter,
) {

    @Transactional
    @ReadOnly
    fun get(year: Int?): List<ProjectRoleUserDTO> {
        val authentication = securityService.checkAuthentication()
        val userId = authentication.id()
        val oneMonthDateRange = oneMonthTimeIntervalFromCurrentYear()
        val timeIntervalForRemainingCalculation = getTimeIntervalForRemainingCalculation(year)

        val requestedYearActivities =
            activityRepository.findOfLatestProjects(timeIntervalForRemainingCalculation.start, timeIntervalForRemainingCalculation.end, userId)
        val lastMonthActivities =
            activityRepository.findOfLatestProjects(oneMonthDateRange.start, oneMonthDateRange.end, userId).map(Activity::toDomain)

        val latestUserProjectRoles =
            lastMonthActivities.sortedByDescending { it.timeInterval.start }.map { it.projectRole }.distinct()
                .map { projectRole ->
                    val remainingOfProjectRoleForUser = activityCalendarService.getRemainingOfProjectRoleForUser(
                        projectRole,
                        requestedYearActivities.map(Activity::toDomain),
                        timeIntervalForRemainingCalculation.getDateInterval(),
                        userId
                    )
                    projectRoleConverter.toProjectRoleUser(projectRole, remainingOfProjectRoleForUser, userId)
                }

        return latestUserProjectRoles.map(projectRoleResponseConverter::toProjectRoleUserDTO)
    }

    private fun oneMonthTimeIntervalFromCurrentYear(): TimeInterval {
        val now = LocalDate.now()

        return TimeInterval.of(
            now.minusMonths(1).atTime(LocalTime.MIN),
            now.atTime(23, 59, 59)
        )
    }

    private fun getTimeIntervalForRemainingCalculation(year: Int?): TimeInterval =
        TimeInterval.ofYear(year ?: LocalDate.now().year)

}