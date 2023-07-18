package com.autentia.tnt.binnacle.validators

import com.autentia.tnt.binnacle.core.domain.Activity
import com.autentia.tnt.binnacle.core.domain.Project
import com.autentia.tnt.binnacle.core.domain.TimeInterval
import com.autentia.tnt.binnacle.core.domain.User
import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.TimeUnit
import com.autentia.tnt.binnacle.exception.*
import com.autentia.tnt.binnacle.services.ActivityCalendarService
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.ProjectService
import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import java.time.LocalDateTime
import javax.transaction.Transactional

@Singleton
internal class ActivityValidator(
    private val activityService: ActivityService,
    private val activityCalendarService: ActivityCalendarService,
    private val projectService: ProjectService,
) {
    @Transactional
    @ReadOnly
    fun checkActivityIsValidForCreation(activityToCreate: Activity, user: User) {
        require(activityToCreate.id == null) { "Cannot create a new activity with id ${activityToCreate.id}." }
        val project = projectService.findById(activityToCreate.projectRole.project.id)
        val emptyActivity = Activity.emptyActivity(activityToCreate.projectRole, user)
        val activityToCreateStartYear = activityToCreate.getYearOfStart()
        val activityToCreateEndYear = activityToCreate.timeInterval.end.year
        val totalRegisteredDurationForThisRoleStartYear =
            getTotalRegisteredDurationByProjectRole(emptyActivity, activityToCreateStartYear, user.id)
        val totalRegisteredDurationForThisRoleEndYear =
            getTotalRegisteredDurationByProjectRole(emptyActivity, activityToCreateEndYear, user.id)

        when {
            isEvidenceInputIncoherent(activityToCreate) -> throw NoEvidenceInActivityException("Activity sets hasEvidence to true but no evidence was found")
            !isProjectOpen(project) -> throw ProjectClosedException()
            !isOpenPeriod(activityToCreate.timeInterval.start) -> throw ActivityPeriodClosedException()
            isProjectBlocked(project, activityToCreate) -> throw ProjectBlockedException(project.blockDate!!)
            isOverlappingAnotherActivityTime(activityToCreate, user.id) -> throw OverlapsAnotherTimeException()
            user.isBeforeHiringDate(activityToCreate.timeInterval.start.toLocalDate()) ->
                throw ActivityBeforeHiringDateException()
            activityToCreate.isMoreThanOneDay() && activityToCreate.timeUnit === TimeUnit.MINUTES -> throw ActivityPeriodNotValidException()
            isExceedingMaxTimePerActivity(activityToCreate) -> throw MaxTimePerActivityRoleException(
                activityToCreate.projectRole.timeInfo.maxTimeAllowed.byActivity,
                activityToCreate.projectRole.timeInfo.timeUnit
            )

            isExceedingMaxHoursForRole(
                emptyActivity,
                activityToCreate,
                activityToCreateStartYear,
                totalRegisteredDurationForThisRoleStartYear
            ) -> throw MaxHoursPerRoleException(
                activityToCreate.projectRole.getMaxTimeAllowedByYear() / DECIMAL_HOUR,
                getRemaining(
                    activityToCreate,
                    totalRegisteredDurationForThisRoleStartYear
                ),
                activityToCreateStartYear
            )

            (activityToCreateStartYear != activityToCreateEndYear) && isExceedingMaxHoursForRole(
                emptyActivity,
                activityToCreate,
                activityToCreateEndYear,
                totalRegisteredDurationForThisRoleEndYear
            ) -> throw MaxHoursPerRoleException(
                activityToCreate.projectRole.getMaxTimeAllowedByYear() / DECIMAL_HOUR,
                getRemaining(
                    activityToCreate,
                    totalRegisteredDurationForThisRoleEndYear
                ),
                activityToCreateEndYear
            )
        }
    }

    private fun isExceedingMaxTimePerActivity(activityToCreate: Activity): Boolean {
        val activityInterval = TimeInterval.of(activityToCreate.getStart(), activityToCreate.getEnd())
        val calendar = activityCalendarService.createCalendar(activityInterval.getDateInterval())

        val activityDuration = activityToCreate.getDuration(calendar)
        return activityToCreate.projectRole.timeInfo.maxTimeAllowed.byActivity > 0 &&
                activityDuration > activityToCreate.projectRole.timeInfo.maxTimeAllowed.byActivity
    }

    private fun isEvidenceInputIncoherent(activity: Activity): Boolean {
        return activity.hasEvidences && activity.evidence == null
                || !activity.hasEvidences && activity.evidence != null
    }

    private fun getTotalRegisteredDurationByProjectRole(
        activityToUpdate: Activity,
        year: Int,
        userId: Long,
    ): Int {
        val yearTimeInterval = TimeInterval.ofYear(year)

        val yearCalendar = activityCalendarService.createCalendar(yearTimeInterval.getDateInterval())

        val activities =
            activityService.getActivitiesByProjectRoleIds(
                yearTimeInterval,
                listOf(activityToUpdate.projectRole.id),
                userId
            )
        return activities.sumOf { it.getDuration(yearCalendar) }
    }

    private fun getTotalRegisteredDurationForThisRoleAfterSave(
        currentActivity: Activity,
        activityToUpdate: Activity,
        year: Int,
        totalRegisteredDurationForThisRole: Int,
    ): Int {
        val yearTimeInterval = TimeInterval.ofYear(year)
        val yearCalendar = activityCalendarService.createCalendar(yearTimeInterval.getDateInterval())
        val currentActivityDuration = currentActivity.getDuration(yearCalendar)
        val activityToUpdateDuration = activityToUpdate.getDuration(yearCalendar)

        var totalRegisteredDurationForThisRoleAfterDiscount = totalRegisteredDurationForThisRole

        if (currentActivity.projectRole.id == activityToUpdate.projectRole.id) {
            totalRegisteredDurationForThisRoleAfterDiscount =
                totalRegisteredDurationForThisRole - currentActivityDuration
        }
        return totalRegisteredDurationForThisRoleAfterDiscount + activityToUpdateDuration
    }

    private fun getRemaining(
        activityToUpdate: Activity,
        totalRegisteredDurationForThisRole: Int,
    ): Double {
        return (activityToUpdate.projectRole.getMaxTimeAllowedByYear() - totalRegisteredDurationForThisRole.toDouble()) / DECIMAL_HOUR
    }

    private fun isExceedingMaxHoursForRole(
        currentActivity: Activity,
        activityToUpdate: Activity,
        year: Int,
        totalRegisteredDurationForThisRole: Int,
    ): Boolean {
        if (activityToUpdate.projectRole.getMaxTimeAllowedByYear() > 0) {
            val totalRegisteredDurationForThisRoleAfterSave = getTotalRegisteredDurationForThisRoleAfterSave(
                currentActivity,
                activityToUpdate,
                year,
                totalRegisteredDurationForThisRole
            )
            return totalRegisteredDurationForThisRoleAfterSave > activityToUpdate.projectRole.getMaxTimeAllowedByYear()
        }
        return false
    }

    private fun isOpenPeriod(startDate: LocalDateTime): Boolean {
        return startDate.year >= LocalDateTime.now().year - 1
    }

    @Transactional
    @ReadOnly
    fun checkActivityIsValidForUpdate(
        activityToUpdate: Activity,
        currentActivity: Activity,
        user: User,
    ) {
        require(activityToUpdate.id != null) { "Cannot update an activity without id." }
        require(currentActivity.approvalState != ApprovalState.ACCEPTED) { "Cannot update an activity already approved." }
        val projectToUpdate = projectService.findById(activityToUpdate.projectRole.project.id)
        val currentProject = projectService.findById(currentActivity.projectRole.project.id)
        val activityToUpdateStartYear = activityToUpdate.getYearOfStart()
        val activityToUpdateEndYear = activityToUpdate.timeInterval.end.year
        val totalRegisteredDurationForThisRoleStartYear =
            getTotalRegisteredDurationByProjectRole(activityToUpdate, activityToUpdateStartYear, user.id)
        val totalRegisteredDurationForThisRoleEndYear =
            getTotalRegisteredDurationByProjectRole(activityToUpdate, activityToUpdateEndYear, user.id)
        when {
            isEvidenceInputIncoherent(activityToUpdate) -> throw NoEvidenceInActivityException("Activity sets hasEvidence to true but no evidence was found")

            isProjectBlocked(
                projectToUpdate,
                activityToUpdate
            ) -> throw ProjectBlockedException(projectToUpdate.blockDate!!)

            isProjectBlocked(
                currentProject,
                currentActivity
            ) -> throw ProjectBlockedException(currentProject.blockDate!!)

            !activityToUpdate.projectRole.project.open -> throw ProjectClosedException()
            !isOpenPeriod(activityToUpdate.timeInterval.start) -> throw ActivityPeriodClosedException()
            isOverlappingAnotherActivityTime(activityToUpdate, user.id) -> throw OverlapsAnotherTimeException()
            user.isBeforeHiringDate(activityToUpdate.timeInterval.start.toLocalDate()) ->
                throw ActivityBeforeHiringDateException()

            activityToUpdate.isMoreThanOneDay() && activityToUpdate.timeUnit === TimeUnit.MINUTES -> throw ActivityPeriodNotValidException()

            isExceedingMaxHoursForRole(
                currentActivity,
                activityToUpdate,
                activityToUpdateStartYear,
                totalRegisteredDurationForThisRoleStartYear
            ) -> throw MaxHoursPerRoleException(
                activityToUpdate.projectRole.getMaxTimeAllowedByYear() / DECIMAL_HOUR,
                getRemaining(
                    activityToUpdate,
                    totalRegisteredDurationForThisRoleStartYear
                ),
                activityToUpdateStartYear
            )

            (activityToUpdateStartYear != activityToUpdateEndYear) && isExceedingMaxHoursForRole(
                currentActivity,
                activityToUpdate,
                activityToUpdateEndYear,
                totalRegisteredDurationForThisRoleEndYear
            ) -> throw MaxHoursPerRoleException(
                activityToUpdate.projectRole.getMaxTimeAllowedByYear() / DECIMAL_HOUR,
                getRemaining(
                    activityToUpdate,
                    totalRegisteredDurationForThisRoleEndYear
                ),
                activityToUpdateEndYear
            )
        }
    }

    @Transactional
    @ReadOnly
    fun checkActivityIsValidForDeletion(activity: Activity) {
        val project = projectService.findById(activity.projectRole.project.id)
        require(activity.approvalState != ApprovalState.ACCEPTED) { "Cannot delete an activity already approved." }
        when {
            isProjectBlocked(project, activity) -> throw ProjectBlockedException(project.blockDate!!)
            !isOpenPeriod(activity.getStart()) -> throw ActivityPeriodClosedException()
        }
    }

    private fun isProjectOpen(project: Project): Boolean {
        return project.open
    }

    private fun isProjectBlocked(project: Project, activity: Activity): Boolean {
        if (project.blockDate == null) {
            return false
        }
        return project.blockDate.isAfter(
            activity.getStart().toLocalDate()
        ) || project.blockDate.isEqual(activity.getStart().toLocalDate())
    }

    private fun isOverlappingAnotherActivityTime(
        activity: Activity,
        userId: Long,
    ): Boolean {
        if (activity.duration == 0) {
            return false
        }
        val activities = activityService.findOverlappedActivities(activity.getStart(), activity.getEnd(), userId)
        return activities.size > 1 || activities.size == 1 && activities[0].id != activity.id
    }

    fun checkActivityIsValidForApproval(activity: Activity) {
        when {
            activity.approvalState == ApprovalState.ACCEPTED || activity.approvalState == ApprovalState.NA -> throw InvalidActivityApprovalStateException()
            !activity.hasEvidences -> throw NoEvidenceInActivityException(activity.id!!)
        }
    }

    private companion object {
        private const val DECIMAL_HOUR = 60.0
    }
}