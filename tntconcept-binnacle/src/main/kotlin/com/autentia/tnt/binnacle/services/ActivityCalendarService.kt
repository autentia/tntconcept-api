package com.autentia.tnt.binnacle.services

import com.autentia.tnt.binnacle.core.domain.*
import com.autentia.tnt.binnacle.core.domain.ActivitiesCalendarFactory
import com.autentia.tnt.binnacle.core.domain.CalendarFactory
import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month
import javax.transaction.Transactional
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Singleton
internal class ActivityCalendarService(
    private val calendarFactory: CalendarFactory,
    private val activitiesCalendarFactory: ActivitiesCalendarFactory,
) {

    @Transactional
    @ReadOnly
    fun createCalendar(dateInterval: DateInterval) = calendarFactory.create(dateInterval)

    @Transactional
    @ReadOnly
    fun getActivityDurationSummaryInHours(
        activities: List<Activity>,
        dateInterval: DateInterval
    ): List<DailyWorkingTime> {
        val activitiesCalendarMap = getActivityCalendarMap(activities, dateInterval)
        return activitiesCalendarMap.toList().map {
            toDailyWorkingTime(it)
        }
    }

    private fun toDailyWorkingTime(activitiesInDate: Pair<LocalDate, List<Activity>>): DailyWorkingTime {
        return if (activitiesInDate.second.isNotEmpty()) {
            DailyWorkingTime(
                activitiesInDate.first,
                getActivitiesDurationInHours(activitiesInDate.second)
            )
        } else {
            DailyWorkingTime(
                activitiesInDate.first,
                BigDecimal.ZERO.setScale(2)
            )
        }
    }

    private fun getActivitiesDurationInHours(activity: List<Activity>): BigDecimal =
        BigDecimal(this.getDurationByCountingNumberOfDays(activity, 1)).divide(
            MINUTES_IN_HOUR, 10, RoundingMode.HALF_UP
        ).setScale(2, RoundingMode.DOWN)

    @Transactional
    @ReadOnly
    fun getActivityDurationByMonth(
        activities: List<Activity>, dateInterval: DateInterval
    ): Map<Month, Duration> {
        val activityCalendarMap = getActivityCalendarMap(activities, dateInterval)
        return activityCalendarMap.toList().filter { it.second.isNotEmpty() }.groupBy { it.first.month }.mapValues {
            getActivitiesDuration(it.value.flatMap { dateActivityPair -> dateActivityPair.second })
        }
    }

    @Transactional
    @ReadOnly
    fun getActivityDurationByMonthlyRoles(
        activities: List<Activity>,
        dateInterval: DateInterval
    ): Map<Month, List<MonthlyRoles>> {
        val activityCalendarMap = getActivityCalendarMap(activities, dateInterval)
        return activityCalendarMap.toList().filter { it.second.isNotEmpty() }.groupBy { it.first.month }.mapValues {
            it.value.flatMap { dateActivityPair -> dateActivityPair.second }
                .groupBy { activity -> activity.projectRole.id }
                .map { projectRole -> toMonthlyRoles(projectRole.key, projectRole.value) }
        }
    }

    private fun toMonthlyRoles(projectRoleId: Long, activities: List<Activity>) =
        MonthlyRoles(projectRoleId, getActivitiesDuration(activities))

    private fun getActivitiesDuration(activities: List<Activity>) =
        this.getDurationByCountingNumberOfDays(activities, 1).minutes

    @Transactional
    @ReadOnly
    fun getActivityCalendarMap(activities: List<Activity>, dateInterval: DateInterval): Map<LocalDate, List<Activity>> {
        val activitiesCalendar = activitiesCalendarFactory.create(dateInterval)
        activitiesCalendar.addAllActivities(activities)
        return activitiesCalendar.activitiesCalendarMap
    }

    fun getRemainingGroupedByProjectRoleAndUser(
        activities: List<Activity>, dateInterval: DateInterval
    ): List<ProjectRoleUser> =
        getRemainingGroupedByProjectRoleAndUser(activities, dateInterval, null)

    fun getRemainingGroupedByProjectRoleAndUser(
        activities: List<Activity>, dateInterval: DateInterval, filterTimeInterval: TimeInterval?
    ): List<ProjectRoleUser> {
        val calendar = createCalendar(dateInterval)

        val filteredActivities = filterActivitiesByTimeInterval(filterTimeInterval, activities)

        return filteredActivities.groupBy { activity -> activity.projectRole }
            .mapValues { projectRoleActivities -> projectRoleActivities.value.groupBy { activity -> activity.userId } }
            .map { userActivitiesGroupedByProjectRole ->
                userActivitiesGroupedByProjectRole.value.map { userActivities ->
                    val projectRole = userActivitiesGroupedByProjectRole.key
                    ProjectRoleUser(
                        projectRole.id,
                        projectRole.name,
                        projectRole.project.organization.id,
                        projectRole.project.id,
                        projectRole.getMaxAllowedInUnits(),
                        projectRole.getRemainingInUnits(calendar, userActivities.value),
                        projectRole.timeUnit,
                        projectRole.requireEvidence,
                        projectRole.isApprovalRequired,
                        userActivities.key
                    )
                }
            }.flatten()
    }

    private fun filterActivitiesByTimeInterval(
        filterTimeInterval: TimeInterval?,
        activities: List<Activity>
    ) = if (filterTimeInterval != null) {
        activities.filter { it.isInTheTimeInterval(filterTimeInterval) }.toList()
    } else {
        activities
    }

    @Transactional
    @ReadOnly
    fun getDurationByCountingWorkingDays(activity: Activity): Int {
        val calendar = calendarFactory.create(activity.getDateInterval())
        return activity.getDurationByCountingWorkableDays(calendar)
    }

    fun getDurationByCountingNumberOfDays(activities: List<Activity>, numberOfDays: Int) =
        activities.sumOf { getDurationByCountingNumberOfDays(it, numberOfDays) }

    fun getDurationByCountingNumberOfDays(activity: Activity, numberOfDays: Int) =
        activity.getDurationByCountingDays(numberOfDays)

    private companion object {
        private val MINUTES_IN_HOUR = BigDecimal(60)
    }
}