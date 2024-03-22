package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.Project
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.criteria.Specification
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDate

@Repository
internal interface ProjectRepository : CrudRepository<Project, Long> {

    fun findAll(projectSpecification: Specification<Project>): List<Project>
    fun findAllByOrganizationId(id: Long): List<Project>

    @Query(value = "UPDATE Project SET blockDate = :date WHERE open = 1", nativeQuery = true)
    fun blockOpenProjects(date: LocalDate)
}
