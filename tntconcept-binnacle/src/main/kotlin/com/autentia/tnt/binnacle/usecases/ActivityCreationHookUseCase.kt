package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.ActivityRequestBodyConverter
import com.autentia.tnt.binnacle.converters.ActivityResponseConverter
import com.autentia.tnt.binnacle.entities.dto.ActivityRequestBodyHookDTO
import com.autentia.tnt.binnacle.entities.dto.ActivityResponseDTO
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.UserService
import com.autentia.tnt.binnacle.validators.ActivityValidator
import io.micronaut.validation.Validated
import jakarta.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
open class ActivityCreationHookUseCase internal constructor(
    private val activityService: ActivityService,
    private val userService: UserService,
    private val activityValidator: ActivityValidator,
    private val activityRequestBodyConverter: ActivityRequestBodyConverter,
    private val activityResponseConverter: ActivityResponseConverter
) {

    fun createActivity(@Valid activityRequestBody: ActivityRequestBodyHookDTO): ActivityResponseDTO {
        val user = userService.getUserByUserName(activityRequestBody.userName)
        val activityRequest = activityRequestBodyConverter
            .mapActivityRequestBodyDTOToActivityRequestBody(activityRequestBody)

        activityValidator.checkActivityIsValidForCreation(activityRequest, user)
        val activityResponse = activityResponseConverter.mapActivityToActivityResponse(
            activityService.createActivity(
                activityRequest,
                user
            )
        )

        return activityResponseConverter.toActivityResponseDTO(activityResponse)
    }

}