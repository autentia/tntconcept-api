package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.core.domain.ActivityTimeOnly
import com.autentia.tnt.binnacle.entities.Activity
import com.autentia.tnt.binnacle.entities.ApprovalState
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.annotation.EntityGraph
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime

@Repository
internal interface ActivityRepository : CrudRepository<Activity, Long> {

    @Query("SELECT a FROM Activity a WHERE a.id= :id")
    @EntityGraph(value = "fetch-activity-with-project-and-organization")
    fun findByIdEager(id: Long): Activity?

    @Query("SELECT a FROM Activity a WHERE a.userId= :userId AND a.approvalState= :approvalState")
    @EntityGraph(value = "fetch-activity-with-project-and-organization")
    fun getActivitiesApprovalState(approvalState: ApprovalState, userId: Long): List<Activity>

    @Query("SELECT a FROM Activity a WHERE a.userId= :userId AND (a.start BETWEEN :start AND :end OR a.end BETWEEN :start AND :end)")
    @EntityGraph(value = "fetch-activity-with-project-and-organization")
    fun getActivitiesBetweenDate(start: LocalDateTime, end: LocalDateTime, userId: Long): List<Activity>

    @Query(
        "SELECT a.start AS start, a.end AS end, a.duration AS duration, a.projectRole.id AS projectRoleId " +
                "FROM Activity a WHERE a.userId= :userId AND a.start BETWEEN :start AND :end"
    )
    fun workedMinutesBetweenDate(start: LocalDateTime, end: LocalDateTime, userId: Long): List<ActivityTimeOnly>

}
