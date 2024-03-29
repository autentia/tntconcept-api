package com.autentia.tnt.binnacle.core.domain


import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.RequireEvidence
import com.autentia.tnt.binnacle.entities.TimeUnit

private const val HOURS_BY_DAY = 8
private const val MINUTES_IN_HOUR = 60

data class ProjectRole(
    val id: Long,
    val name: String,
    val requireEvidence: RequireEvidence,
    val project: Project,
    val isWorkingTime: Boolean,
    val isApprovalRequired: Boolean,
    val timeInfo: TimeInfo,
) {
    fun getRemainingInUnits(calendar: Calendar, activities: List<Activity>): Int {
        val remaining = getRemaining(calendar, activities)
        if (timeInfo.timeUnit === TimeUnit.DAYS || timeInfo.timeUnit === TimeUnit.NATURAL_DAYS) {
            return fromMinutesToDays(remaining)
        }
        return remaining
    }

    fun isMaxTimeAllowedRole() = getMaxTimeAllowedByYear() > 0

    fun getMaxTimeAllowedByYear() = timeInfo.getMaxTimeAllowedByYear()

    fun getMaxTimeAllowedByActivity() = timeInfo.getMaxTimeAllowedByActivity()

    fun getTimeUnit() = timeInfo.timeUnit

    fun getMaxTimeAllowedByYearInTimeUnits(): Double {
        return when (timeInfo.timeUnit) {
            TimeUnit.DAYS -> timeInfo.maxTimeAllowed.byYear / 60.0 / 8.0
            TimeUnit.NATURAL_DAYS -> timeInfo.maxTimeAllowed.byYear / 60.0 / 8.0
            TimeUnit.MINUTES -> timeInfo.maxTimeAllowed.byYear.toDouble()
        }
    }

    fun getMaxTimeAllowedByActivityInTimeUnits(): Int {
        return when (timeInfo.timeUnit) {
            TimeUnit.DAYS -> timeInfo.maxTimeAllowed.byActivity / 60 / 8
            TimeUnit.NATURAL_DAYS -> timeInfo.maxTimeAllowed.byActivity / 60 / 8
            TimeUnit.MINUTES -> timeInfo.maxTimeAllowed.byActivity
        }
    }

    fun getApprovalState() = if (isApprovalRequired) ApprovalState.PENDING else ApprovalState.NA

    fun requireEvidence() = RequireEvidence.isRequired(requireEvidence)

    private fun getRemaining(calendar: Calendar, activities: List<Activity>) =
        if (isMaxTimeAllowedRole()) {
            timeInfo.getMaxTimeAllowedByYear() - activities.sumOf { activity -> activity.getDuration(calendar) }
        } else {
            0
        }

    private fun fromMinutesToDays(minutes: Int) = minutes / (MINUTES_IN_HOUR * HOURS_BY_DAY)
}
