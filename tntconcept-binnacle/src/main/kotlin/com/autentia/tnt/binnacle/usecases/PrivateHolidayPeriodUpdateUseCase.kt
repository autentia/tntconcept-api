package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.CreateVacationResponseConverter
import com.autentia.tnt.binnacle.converters.RequestVacationConverter
import com.autentia.tnt.binnacle.entities.dto.CreateVacationResponseDTO
import com.autentia.tnt.binnacle.entities.dto.RequestVacationDTO
import com.autentia.tnt.binnacle.exception.*
import com.autentia.tnt.binnacle.services.UserService
import com.autentia.tnt.binnacle.services.VacationMailService
import com.autentia.tnt.binnacle.services.VacationService
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation
import com.autentia.tnt.binnacle.validators.VacationValidator
import jakarta.inject.Singleton
import java.util.*
import javax.transaction.Transactional

@Singleton
class PrivateHolidayPeriodUpdateUseCase internal constructor(
    private val userService: UserService,
    private val vacationMailService: VacationMailService,
    private val vacationValidator: VacationValidator,
    private val createVacationResponseConverter: CreateVacationResponseConverter,
    private val requestVacationConverter: RequestVacationConverter,
    private val vacationService: VacationService,
) {

    @Transactional
    fun update(requestVacationDTO: RequestVacationDTO, locale: Locale): CreateVacationResponseDTO {
        require(requestVacationDTO.id != null) { "Cannot create vacation without id." }

        val requestVacation = requestVacationConverter.toRequestVacation(requestVacationDTO)

        val user = userService.getAuthenticatedUser()

        when (val result = vacationValidator.canUpdateVacationPeriod(requestVacation, user)) {
            is UpdateVacationValidation.Success -> {
                val vacation = vacationService.updateVacationPeriod(requestVacation, user, result.vacationDb)
                vacationMailService.sendRequestVacationsMail(
                    user.username,
                    vacation.startDate,
                    vacation.endDate,
                    requestVacation.description.orEmpty(),
                    locale
                )
                return createVacationResponseConverter.toCreateVacationResponseDTO(vacation)
            }

            is UpdateVacationValidation.Failure ->
                when (result.reason) {
                    UpdateVacationValidation.FailureReason.INVALID_DATE_RANGE -> throw DateRangeException(
                        requestVacation.startDate,
                        requestVacation.endDate
                    )

                    UpdateVacationValidation.FailureReason.VACATION_ALREADY_ACCEPTED -> throw VacationAcceptedStateException()
                    UpdateVacationValidation.FailureReason.VACATION_RANGE_CLOSED -> throw VacationRangeClosedException()
                    UpdateVacationValidation.FailureReason.VACATION_NOT_FOUND -> throw VacationNotFoundException(
                        requestVacation.id!!
                    )

                    UpdateVacationValidation.FailureReason.VACATION_BEFORE_HIRING_DATE -> throw VacationBeforeHiringDateException()
                    UpdateVacationValidation.FailureReason.VACATION_REQUEST_OVERLAPS -> throw VacationRequestOverlapsException()
                    UpdateVacationValidation.FailureReason.VACATION_REQUEST_EMPTY -> throw VacationRequestEmptyException()
                    UpdateVacationValidation.FailureReason.NO_MORE_DAYS_LEFT_IN_YEAR -> throw NoMoreDaysLeftInYearException()
                }
        }
    }

}
