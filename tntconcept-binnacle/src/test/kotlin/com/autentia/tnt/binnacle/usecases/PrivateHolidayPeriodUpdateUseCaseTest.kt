package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createUser
import com.autentia.tnt.binnacle.config.createVacationUpdateDTO
import com.autentia.tnt.binnacle.converters.CreateVacationResponseConverter
import com.autentia.tnt.binnacle.converters.RequestVacationConverter
import com.autentia.tnt.binnacle.core.domain.CreateVacationResponse
import com.autentia.tnt.binnacle.entities.Vacation
import com.autentia.tnt.binnacle.entities.VacationState
import com.autentia.tnt.binnacle.entities.dto.RequestVacationDTO
import com.autentia.tnt.binnacle.exception.BinnacleException
import com.autentia.tnt.binnacle.exception.DateRangeException
import com.autentia.tnt.binnacle.exception.UserPermissionException
import com.autentia.tnt.binnacle.exception.VacationAcceptedStateException
import com.autentia.tnt.binnacle.exception.VacationBeforeHiringDateException
import com.autentia.tnt.binnacle.exception.VacationNotFoundException
import com.autentia.tnt.binnacle.exception.VacationRangeClosedException
import com.autentia.tnt.binnacle.exception.VacationRequestEmptyException
import com.autentia.tnt.binnacle.exception.VacationRequestOverlapsException
import com.autentia.tnt.binnacle.services.UserService
import com.autentia.tnt.binnacle.services.VacationMailService
import com.autentia.tnt.binnacle.services.VacationService
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.Success
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.Failure
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.VACATION_REQUEST_EMPTY
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.VACATION_RANGE_CLOSED
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.VACATION_ALREADY_ACCEPTED
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.VACATION_NOT_FOUND
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.VACATION_REQUEST_OVERLAPS
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.VACATION_BEFORE_HIRING_DATE
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.INVALID_DATE_RANGE
import com.autentia.tnt.binnacle.validators.UpdateVacationValidation.FailureReason.USER_UNAUTHORIZED
import com.autentia.tnt.binnacle.validators.VacationValidator
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import java.time.LocalDate
import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
@TestInstance(PER_CLASS)
internal class PrivateHolidayPeriodUpdateUseCaseTest {

    private val vacationService = mock<VacationService>()
    private val userService = mock<UserService>()
    private val vacationMailService = mock<VacationMailService>()
    private val vacationValidator = mock<VacationValidator>()

    private val privateHolidayPeriodUpdateUseCase =
        PrivateHolidayPeriodUpdateUseCase(userService, vacationMailService, vacationValidator, CreateVacationResponseConverter(), RequestVacationConverter(), vacationService)

    @Test
    fun `update an existing vacation period`() {
        doReturn(USER).whenever(userService).getAuthenticatedUser()

        doReturn(Success(vacationToUpdate)).whenever(vacationValidator).canUpdateVacationPeriod(any(), eq(USER))

        doReturn(CREATE_VACATION_RESPONSE).whenever(vacationService).updateVacationPeriod(any(), eq(USER), eq(
            vacationToUpdate
        ))

        privateHolidayPeriodUpdateUseCase.update(REQUEST_VACATION_DTO, Locale.ENGLISH)

        verify(vacationMailService, times(1)).sendRequestVacationsMail( USER.username, TODAY, AFTER_TOMORROW, "Lorem ipsum...", Locale.ENGLISH)
    }
    @Test
    fun `FAIL when the vacation period to update is not found in the database`() {
        doReturn(USER).whenever(userService).getAuthenticatedUser()

        doReturn(Failure(VACATION_NOT_FOUND)).whenever(vacationValidator).canUpdateVacationPeriod(any(), eq(USER))

        assertThrows<VacationNotFoundException> {
            privateHolidayPeriodUpdateUseCase.update(invalidVacationId, Locale.ENGLISH)
        }
    }

    private fun updateVacationFailProvider() = arrayOf(
        arrayOf("Invalid date range exception", DateRangeException(AFTER_TOMORROW, TODAY), Failure(INVALID_DATE_RANGE), invalidVacationRange, DateRangeException(
            AFTER_TOMORROW, TODAY
        ).message),
        arrayOf("User permission exception", UserPermissionException(), Failure(USER_UNAUTHORIZED), notLoggedUserVacation, UserPermissionException().message),
        arrayOf("Vacation accepted exception", VacationAcceptedStateException(), Failure(VACATION_ALREADY_ACCEPTED), vacationAccepted, VacationAcceptedStateException().message),
        arrayOf("Vacation range closed exception", VacationRangeClosedException(), Failure(VACATION_RANGE_CLOSED), vacationForRangeClosed, VacationRangeClosedException().message),
        arrayOf("Vacation before hiring date exception", VacationBeforeHiringDateException(), Failure(VACATION_BEFORE_HIRING_DATE), vacationRequestBeforeHiringDate, VacationBeforeHiringDateException().message),
        arrayOf("Vacation overlaps exception", VacationRequestOverlapsException(), Failure(VACATION_REQUEST_OVERLAPS), overlappingVacation, VacationRequestOverlapsException().message),
        arrayOf("Vacation empty exception", VacationRequestEmptyException(), Failure(VACATION_REQUEST_EMPTY), emptyVacation, VacationRequestEmptyException().message),
    )
    @ParameterizedTest
    @MethodSource("updateVacationFailProvider")
    fun `fail if try to update a vacation and an exception is thrown`(
        testDescription: String,
        expectedException: BinnacleException,
        failureReason: UpdateVacationValidation,
        vacationDTO: RequestVacationDTO,
        expectedExceptionMessage: String
    ) {
        doReturn(USER).whenever(userService).getAuthenticatedUser()
        doReturn(failureReason).whenever(vacationValidator).canUpdateVacationPeriod(any(), eq(USER))

        val exception = assertThrows<BinnacleException> {
            privateHolidayPeriodUpdateUseCase.update(vacationDTO, Locale.ENGLISH)
        }
        assertInstanceOf(expectedException.javaClass, exception)
        assertEquals(expectedExceptionMessage, exception.message)
    }

    private companion object{
        private val USER = createUser()
        private val TODAY = LocalDate.now()
        private val TOMORROW = LocalDate.now().plusDays(1)
        private val AFTER_TOMORROW = TODAY.plusDays(2)
        private val CURRENT_YEAR = LocalDate.now().year
        private val TODAY_TWO_YEARS_AGO = LocalDate.now().minusYears(2)
        private val TOMORROW_TWO_YEARS_AGO = LocalDate.now().minusYears(2).plusDays(1)

        private val CREATE_VACATION_RESPONSE = listOf(
            CreateVacationResponse(
                startDate = TODAY,
                endDate = AFTER_TOMORROW,
                days = 1,
                chargeYear = CURRENT_YEAR
            )
        ).toMutableList()

        private const val INVALID_ID = 20L
        private val vacationToUpdate = Vacation(id = 1L, state = VacationState.ACCEPT, startDate =  TODAY, endDate =  TOMORROW, userId = USER.id, description = "", chargeYear = TODAY)
        private val REQUEST_VACATION_DTO = createVacationUpdateDTO(TODAY, AFTER_TOMORROW)
        private val vacationAccepted = createVacationUpdateDTO(TODAY, AFTER_TOMORROW)
        private val invalidVacationRange = createVacationUpdateDTO(AFTER_TOMORROW, TODAY)
        private val notLoggedUserVacation = createVacationUpdateDTO(TODAY, AFTER_TOMORROW)
        private val vacationForRangeClosed = createVacationUpdateDTO(TODAY_TWO_YEARS_AGO, TOMORROW_TWO_YEARS_AGO)
        private val vacationRequestBeforeHiringDate = createVacationUpdateDTO(USER.hiringDate.minusDays(3), USER.hiringDate.minusDays(2))
        private val emptyVacation = createVacationUpdateDTO(TODAY, TODAY)
        private val invalidVacationId = createVacationUpdateDTO(INVALID_ID, TODAY, AFTER_TOMORROW)
        private val overlappingVacation = createVacationUpdateDTO(startDate = LocalDate.of(2019, 2, 1), endDate = LocalDate.of(2019, 2, 2))

    }

}
