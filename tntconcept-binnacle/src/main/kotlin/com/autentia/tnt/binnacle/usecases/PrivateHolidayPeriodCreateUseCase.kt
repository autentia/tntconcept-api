package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.CreateVacationResponseConverter
import com.autentia.tnt.binnacle.converters.RequestVacationConverter
import com.autentia.tnt.binnacle.entities.dto.CreateVacationResponseDTO
import com.autentia.tnt.binnacle.entities.dto.RequestVacationDTO
import com.autentia.tnt.binnacle.exception.DateRangeException
import com.autentia.tnt.binnacle.exception.VacationBeforeHiringDateException
import com.autentia.tnt.binnacle.exception.VacationRangeClosedException
import com.autentia.tnt.binnacle.exception.VacationRequestEmptyException
import com.autentia.tnt.binnacle.exception.VacationRequestOverlapsException
import com.autentia.tnt.binnacle.services.UserService
import com.autentia.tnt.binnacle.services.VacationMailService
import com.autentia.tnt.binnacle.services.VacationService
import com.autentia.tnt.binnacle.validators.CreateVacationValidation
import com.autentia.tnt.binnacle.validators.VacationValidator
import jakarta.inject.Singleton
import java.util.Locale
import javax.transaction.Transactional

@Singleton
class PrivateHolidayPeriodCreateUseCase internal constructor(
    private val vacationService: VacationService,
    private val userService: UserService,
    private val vacationValidator: VacationValidator,
    private val createVacationResponseConverter: CreateVacationResponseConverter,
    private val requestVacationConverter: RequestVacationConverter,
    private val vacationMailService: VacationMailService,
) {

    @Transactional
    fun create(requestVacationDTO: RequestVacationDTO, locale: Locale): List<CreateVacationResponseDTO> {
        require(requestVacationDTO.id == null) { "Cannot create vacation with id ${requestVacationDTO.id}." }

        val user = userService.getAuthenticatedUser()

        val requestVacation = requestVacationConverter.toRequestVacation(requestVacationDTO)

        when (val result = vacationValidator.canCreateVacationPeriod(requestVacation, user)) {
            is CreateVacationValidation.Success -> {
                val holidays = vacationService.createVacationPeriod(requestVacation, user)
                holidays.forEach {
                    vacationMailService.sendRequestVacationsMail(
                        user.username,
                        it.startDate,
                        it.endDate,
                        requestVacation.description.orEmpty(),
                        locale
                    )
                }
                return holidays.map { createVacationResponseConverter.toCreateVacationResponseDTO(it) }
            }

            is CreateVacationValidation.Failure ->
                when (result.reason) {
                    CreateVacationValidation.FailureReason.INVALID_DATE_RANGE -> throw DateRangeException(
                        requestVacation.startDate,
                        requestVacation.endDate
                    )

                    CreateVacationValidation.FailureReason.VACATION_RANGE_CLOSED -> throw VacationRangeClosedException()
                    CreateVacationValidation.FailureReason.VACATION_BEFORE_HIRING_DATE -> throw VacationBeforeHiringDateException()
                    CreateVacationValidation.FailureReason.VACATION_REQUEST_OVERLAPS -> throw VacationRequestOverlapsException()
                    CreateVacationValidation.FailureReason.VACATION_REQUEST_EMPTY -> throw VacationRequestEmptyException()
                }
        }
    }

}
