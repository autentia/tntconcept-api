package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.core.domain.ActivityTimeOnly
import com.autentia.tnt.binnacle.entities.Activity
import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.repositories.predicates.ActivityPredicates
import com.autentia.tnt.binnacle.repositories.predicates.PredicateBuilder
import com.autentia.tnt.security.application.checkAuthentication
import com.autentia.tnt.security.application.id
import com.autentia.tnt.security.application.isAdmin
import com.autentia.tnt.security.application.isNotAdmin
import io.micronaut.context.annotation.Primary
import io.micronaut.data.jpa.repository.criteria.Specification
import io.micronaut.security.utils.SecurityService
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
@Primary
internal class ActivityRepositorySecured(
    private val activityDao: ActivityDao,
    private val internalActivityRepository: InternalActivityRepository,
    private val securityService: SecurityService,
) : ActivityRepository {

    override fun findAll(activitySpecification: Specification<Activity>): List<Activity> =
        internalActivityRepository.findAll(addUserFilterIfNecessary(activitySpecification))

    override fun findById(id: Long): Activity? {
        val authentication = securityService.checkAuthentication()

        return if (authentication.isAdmin()) {
            internalActivityRepository.findById(id)
        } else {
            internalActivityRepository.findByIdAndUserId(id, authentication.id())
        }
    }

    override fun findByIdWithoutSecurity(id: Long): Activity? {
        //TODO: Add security to this method!!!
        return activityDao.findById(id).orElse(null)
    }

    override fun find(startDate: LocalDateTime, endDate: LocalDateTime): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return internalActivityRepository.find(startDate, endDate, authentication.id())
    }

    override fun findWithoutSecurity(startDate: LocalDateTime, endDate: LocalDateTime, userId: Long): List<Activity> {
        //TODO: Add security to this method!!!
        return activityDao.find(startDate, endDate, userId)
    }

    override fun find(approvalState: ApprovalState): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.isAdmin()) {
            internalActivityRepository.findByApprovalState(approvalState)
        } else {
            internalActivityRepository.findByApprovalStateAndUserId(approvalState, authentication.id())
        }
    }

    override fun find(projectRoleId: Long): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return internalActivityRepository.findByProjectRoleIdAndUserId(projectRoleId, authentication.id())
    }

    override fun find(startDate: LocalDateTime, endDate: LocalDateTime, userIds: List<Long>): List<Activity> {
        val authentication = securityService.checkAuthentication()
        val userIdsFiltered = if (!authentication.isAdmin()) {
            userIds.filter { it == authentication.id() }
        } else {
            userIds
        }

        return activityDao.find(startDate, endDate, userIdsFiltered)
    }

    override fun findOfLatestProjects(start: LocalDateTime, end: LocalDateTime): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return internalActivityRepository.findOfLatestProjects(start, end, authentication.id())
    }

    override fun findByProjectId(start: LocalDateTime, end: LocalDateTime, projectId: Long): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return activityDao.findByProjectId(start, end, projectId, authentication.id())
    }

    override fun findWorkedMinutes(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
    ): List<ActivityTimeOnly> {
        val authentication = securityService.checkAuthentication()
        return activityDao.findWorkedMinutes(startDate, endDate, authentication.id())
    }

    override fun findOverlapped(startDate: LocalDateTime, endDate: LocalDateTime): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return activityDao.findOverlapped(startDate, endDate, authentication.id())
    }

    override fun find(start: LocalDateTime, end: LocalDateTime, projectRoleId: Long): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return activityDao.find(start, end, projectRoleId, authentication.id())
    }

    override fun findByProjectRoleIds(
        start: LocalDateTime, end: LocalDateTime, projectRoleIds: List<Long>
    ): List<Activity> {
        val authentication = securityService.checkAuthentication()
        return activityDao.findByProjectRoleIds(start, end, projectRoleIds, authentication.id())

    }

    override fun save(activity: Activity): Activity {
        val authentication = securityService.checkAuthentication()
        require(activity.userId == authentication.id()) { "User cannot save activity" }

        return activityDao.save(activity)
    }

    override fun saveWithoutSecurity(activity: Activity): Activity {
        //TODO: Temporal method, we have to implement security!!
        return activityDao.save(activity)
    }

    override fun update(activity: Activity): Activity {
        val authentication = securityService.checkAuthentication()

        if (authentication.isNotAdmin()) {
            require(activity.userId == authentication.id()) { "User cannot update activity" }
        }

        val activityToUpdate = activityDao.findById(activity.id)
        require(activityToUpdate.isPresent) { "Activity to update does not exist" }

        return activityDao.update(activity)
    }

    override fun updateWithoutSecurity(activity: Activity): Activity {
        //TODO: Temporal method, we have to implement security!!
        val activityToUpdate = activityDao.findById(activity.id)
        require(activityToUpdate.isPresent) { "Activity to update does not exist" }

        return activityDao.update(activity)
    }

    override fun deleteById(id: Long) {
        val authentication = securityService.checkAuthentication()
        val activityToDelete = activityDao.findById(id)

        require(activityToDelete.isPresent) { "Activity with id $id does not exist" }
        require(activityToDelete.get().userId == authentication.id()) { "User cannot delete activity" }

        activityDao.deleteById(id)
    }

    private fun addUserFilterIfNecessary(activitySpecification: Specification<Activity>): Specification<Activity> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.isNotAdmin()) {
            PredicateBuilder.and(activitySpecification, ActivityPredicates.userId(authentication.id()))
        } else {
            activitySpecification
        }
    }
}