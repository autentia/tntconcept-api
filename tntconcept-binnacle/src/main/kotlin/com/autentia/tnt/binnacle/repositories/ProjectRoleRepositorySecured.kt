package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.core.domain.ProjectRolesRecent
import com.autentia.tnt.binnacle.entities.ProjectRole
import com.autentia.tnt.security.application.checkAuthentication
import com.autentia.tnt.security.application.id
import io.micronaut.security.utils.SecurityService
import jakarta.inject.Singleton
import java.time.LocalDateTime

@Singleton
internal class ProjectRoleRepositorySecured(
    private val projectRoleDao: ProjectRoleDao,
    private val securityService: SecurityService
) : ProjectRoleRepository {
    override fun findById(id: Long): ProjectRole? {
        return projectRoleDao.findById(id).orElse(null)
    }

    override fun getAllByProjectId(id: Long): List<ProjectRole> {
        return projectRoleDao.getAllByProjectId(id)
    }

    override fun getAllByProjectIdIn(ids: List<Long>): List<ProjectRole> {
        return projectRoleDao.getAllByProjectIdIn(ids)
    }

    override fun getAllByIdIn(ids: List<Long>): List<ProjectRole> {
        return projectRoleDao.getAllByIdIn(ids)
    }

    override fun getAllNotWorkable(): List<ProjectRole> {
        return projectRoleDao.findAllByIsWorkingTimeFalse();
    }

    @Deprecated("Use findDistinctRolesBetweenDate instead")
    override fun findDistinctProjectRolesBetweenDate(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ProjectRolesRecent> {
        val authentication = securityService.checkAuthentication()
        return projectRoleDao.findDistinctProjectRolesBetweenDate(startDate, endDate, authentication.id())
    }

}