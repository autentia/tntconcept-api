package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.ActivityRequestBodyConverter
import com.autentia.tnt.binnacle.converters.ActivityResponseConverter
import com.autentia.tnt.binnacle.entities.dto.ActivityRequestBodyDTO
import com.autentia.tnt.binnacle.entities.dto.ActivityResponseDTO
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.UserService
import com.autentia.tnt.binnacle.validators.ActivityValidator
import jakarta.inject.Singleton

@Singleton
class ActivityUpdateUseCase internal constructor(
    private val activityService: ActivityService,
    private val userService: UserService,
    private val activityValidator: ActivityValidator,
    private val activityRequestBodyConverter: ActivityRequestBodyConverter,
    private val activityResponseConverter: ActivityResponseConverter
) {
    fun updateActivity(activityRequest: ActivityRequestBodyDTO): ActivityResponseDTO {
        val user = userService.getAuthenticatedUser()
        val  activityRequestBody = activityRequestBodyConverter.mapActivityRequestBodyDTOToActivityRequestBody(activityRequest)
        activityValidator.checkActivityIsValidForUpdate(activityRequestBody, user)
        return activityResponseConverter.mapActivityToActivityResponseDTO(activityService.updateActivity(activityRequestBody, user))
    }
}
