package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.ActivityDateConverter
import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.DateInterval
import com.autentia.tnt.binnacle.entities.dto.ActivityDateDTO
import com.autentia.tnt.binnacle.services.ActivityService
import com.autentia.tnt.binnacle.services.UserService
import jakarta.inject.Singleton
import java.time.LocalDate
import java.util.Optional

@Singleton
class ActivitiesBetweenDateUseCase internal constructor(
    private val activityService: ActivityService,
    private val userService: UserService,
    private val activityDateConverter: ActivityDateConverter
) {

    fun getActivities(
        start: Optional<LocalDate>, end: Optional<LocalDate>, approvalState: Optional<ApprovalState>
    ): List<ActivityDateDTO> {
        val user = userService.getAuthenticatedUser()

        val activities = if (start.isPresent && end.isPresent) {
            activityService.getActivitiesBetweenDates(DateInterval.of(start.get(), end.get()), user.id)
        } else {
            activityService.getActivitiesApprovalState(approvalState.get(), user.id)
        }
        return activityDateConverter.mapActivitiesToActivitiesDateDTO(activities)
    }
}
