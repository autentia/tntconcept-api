package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.entities.dto.ProjectRoleResponseDTO
import com.autentia.tnt.binnacle.exception.ProjectRoleNotFoundException
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import javax.transaction.Transactional

@Singleton
class ProjectRoleByIdUseCase internal constructor(
    private val projectRoleRepository: ProjectRoleRepository,
    private val projectRoleResponseConverter: ProjectRoleResponseConverter
) {

    @Transactional
    @ReadOnly
    fun get(id: Long): ProjectRoleResponseDTO =
        projectRoleRepository
            .findById(id)
            .map { projectRoleResponseConverter.toProjectRoleResponseDTO(it) }
            .orElseThrow { ProjectRoleNotFoundException(id) }

}
