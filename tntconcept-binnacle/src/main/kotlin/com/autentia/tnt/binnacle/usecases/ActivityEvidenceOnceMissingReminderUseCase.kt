package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.entities.*
import com.autentia.tnt.binnacle.repositories.predicates.ActivityPredicates.belongsToUsers
import com.autentia.tnt.binnacle.repositories.predicates.ActivityPredicates.hasNotEvidence
import com.autentia.tnt.binnacle.repositories.predicates.ActivityPredicates.projectRoleRequiresEvidence
import com.autentia.tnt.binnacle.repositories.predicates.PredicateBuilder
import com.autentia.tnt.binnacle.services.ActivityEvidenceMissingMailService
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.UserService
import io.micronaut.data.jpa.repository.criteria.Specification
import jakarta.inject.Singleton
import java.util.*

@Singleton
class ActivityEvidenceOnceMissingReminderUseCase internal constructor(
        private val activityService: ActivityService,
        private val activityEvidenceMissingMailService: ActivityEvidenceMissingMailService,
        private val userService: UserService,
) {
    fun sendReminders() {
        val rolesMissingEvidenceByUser: Map<User, List<ProjectRole>> = getProjectRolesMissingEvidenceByUser()
        rolesMissingEvidenceByUser.forEach { (user, rolesMissingEvidence) ->
            notifyMissingEvidencesToUser(user, rolesMissingEvidence)
        }
    }

    private fun getProjectRolesMissingEvidenceByUser(): Map<User, List<ProjectRole>> {
        val activeUsers: List<User> = userService.findActive()
        val activitiesMissingEvidence: List<Activity> = activityService.getActivities(
                getActivitiesMissingEvidenceOncePredicate(activeUsers.map { it.id }.toList())
        )

        val activitiesMissingEvidenceByUser = mutableMapOf<User, List<Activity>>()
        for (user in activeUsers) {
            activitiesMissingEvidenceByUser[user] = activitiesMissingEvidence.filter { it.userId == user.id }
        }

        return activitiesMissingEvidenceByUser.mapValues { activitiesByUser ->
            activitiesByUser.value.map { it.projectRole }.distinct()
        }
    }

    private fun getActivitiesMissingEvidenceOncePredicate(listOfUserIds: List<Long>): Specification<Activity> {
        return PredicateBuilder.and(
                PredicateBuilder.and(hasNotEvidence(), projectRoleRequiresEvidence(RequireEvidence.ONCE)),
                belongsToUsers(listOfUserIds)
        )
    }

    private fun notifyMissingEvidencesToUser(user: User, rolesMissingEvidence: List<ProjectRole>) {
        val rolesGroupedByProject: Map<Project, List<ProjectRole>> = rolesMissingEvidence.groupBy { it.project }
        rolesGroupedByProject.forEach { notifyMissingProjectEvidenceToUser(user, it.key, it.value) }
    }

    private fun notifyMissingProjectEvidenceToUser(user: User, project: Project, projectRoleList: List<ProjectRole>) {
        val projectRoleNames = projectRoleList.map { it.name }.toSet()
        activityEvidenceMissingMailService.sendEmail(
                project.organization.name,
                project.name,
                projectRoleNames,
                user.email,
                Locale.forLanguageTag("es")
        )
    }
}