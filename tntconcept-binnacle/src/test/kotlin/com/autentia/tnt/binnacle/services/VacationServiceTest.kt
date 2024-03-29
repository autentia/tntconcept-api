package com.autentia.tnt.binnacle.services

import com.autentia.tnt.binnacle.config.createUser
import com.autentia.tnt.binnacle.converters.VacationConverter
import com.autentia.tnt.binnacle.core.domain.CalendarFactory
import com.autentia.tnt.binnacle.core.domain.DateInterval
import com.autentia.tnt.binnacle.core.domain.RequestVacation
import com.autentia.tnt.binnacle.entities.Holiday
import com.autentia.tnt.binnacle.entities.Vacation
import com.autentia.tnt.binnacle.entities.VacationState
import com.autentia.tnt.binnacle.entities.VacationState.ACCEPT
import com.autentia.tnt.binnacle.entities.VacationState.PENDING
import com.autentia.tnt.binnacle.repositories.HolidayRepository
import com.autentia.tnt.binnacle.repositories.VacationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.Mockito.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.DayOfWeek.MONDAY
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month.*
import java.time.temporal.TemporalAdjusters

@TestInstance(PER_CLASS)
internal class VacationServiceTest {

    private val holidayRepository = mock<HolidayRepository>()
    private val vacationRepository = mock<VacationRepository>()
    private val vacationConverter = VacationConverter()
    private val remainingVacationService = mock<RemainingVacationService>()

    private val calendarFactory = CalendarFactory(holidayRepository)

    private val vacationService = VacationService(
        vacationRepository, vacationConverter, calendarFactory, remainingVacationService
    )

    @Test
    fun `return all vacations between date`() {
        val vacation =
            createVacation(startDate = FIRST_DAY_2020, endDate = JAN_TENTH_2020, state = PENDING, userId = USER.id)

        doReturn(listOf(vacation)).whenever(vacationRepository)
            .find(FIRST_DAY_2020, END_DATE)

        doReturn(holidays2020).whenever(holidayRepository).findAllByDateBetween(FIRST_DAY_2020.atTime(LocalTime.MIN), JAN_TENTH_2020.atTime(23, 59, 59))
        val vacationsBetweenDates =
            vacationService.getVacationsBetweenDates(beginDate = FIRST_DAY_2020, finalDate = END_DATE)
        assertThat(vacationsBetweenDates[0].days).hasSize(6)
    }

    @Test
    fun `return vacations between date when no exist vacations `() {
        doReturn(EMPTY_VACATIONS).whenever(vacationRepository)
            .find(FIRST_DAY_2020, END_DATE)

        val vacations =
            vacationService.getVacationsBetweenDates(beginDate = FIRST_DAY_2020, finalDate = END_DATE)

        assertThat(vacations.sumOf { it.days.size }).isEqualTo(0)
    }

    @Test
    fun `return all vacations by charge year`() {
        doReturn(vacations2020).whenever(vacationRepository)
            .findBetweenChargeYears(FIRST_DAY_2020, FIRST_DAY_2020)

        doReturn(holidays2020).whenever(holidayRepository).findAllByDateBetween(FIRST_DAY_2020.atTime(LocalTime.MIN), FOURTH_FEB_2020.atTime(23, 59, 59))
        val vacations = vacationService.getVacationsByChargeYear(YEAR_2020)
        assertThat(vacations.sumOf { it.days.size }).isEqualTo(8)
    }

    @Test
    fun `return all vacations by charge year when vacations are empty`() {
        doReturn(EMPTY_VACATIONS).whenever(vacationRepository)
            .findBetweenChargeYears(FIRST_DAY_2020, FIRST_DAY_2020)

        val actual = vacationService.getVacationsByChargeYear(YEAR_2020)

        assertThat(actual.sumOf { it.days.size }).isEqualTo(0)
    }

    @Test
    fun `return vacation even if the begin and end date are equal`() {
        val vacation = createVacation(startDate = APR_NINTH_2020, endDate = APR_NINTH_2020, state = ACCEPT)

        doReturn(listOf(vacation)).whenever(vacationRepository)
            .find(APR_FIRST_2020, APR_THIRTEENTH_2020)

        val vacationsBetweenDates = vacationService.getVacationsBetweenDates(APR_FIRST_2020, APR_THIRTEENTH_2020)

        assertThat(vacationsBetweenDates[0].days).hasSize(1)
        assertEquals(vacationsBetweenDates[0].days[0], APR_NINTH_2020)
    }

    @Test
    fun `create a valid vacation period`() {

        doReturn(23)
            .whenever(remainingVacationService).getRemainingVacations(eq(CURRENT_YEAR), eq(USER))

        val selectedDays = mockRequestVacation(REQUEST_8_DAYS_IN_JANUARY)

        val actual = vacationService.createVacationPeriod(REQUEST_8_DAYS_IN_JANUARY, USER)

        assertEquals(selectedDays.first(), actual.startDate)
        assertEquals(selectedDays.last(), actual.endDate)
        assertEquals(selectedDays.size, actual.days)
        assertEquals(CURRENT_YEAR, actual.chargeYear)
    }

    @Test
    fun `update vacation period when the new corresponding days quantity IS EQUAL to old corresponding days quantity`() {
        val vacation = createVacation(
            id = VACATION_ID,
            startDate = FIRST_MONDAY.plusDays(2),
            endDate = FIRST_MONDAY.plusDays(3),
            userId = USER.id,
            chargeYear = FIRST_MONDAY.withDayOfYear(1)
        )

        val requestVacation = RequestVacation(
            id = VACATION_ID,
            startDate = FIRST_MONDAY,
            endDate = FIRST_MONDAY.plusDays(1),
            chargeYear = CURRENT_YEAR,
            description = "asdasd"
        )

        val newPrivateHoliday = vacation.copy(
            startDate = requestVacation.startDate,
            endDate = requestVacation.endDate,
            description = requestVacation.description ?: ""
        )

        doReturn(newPrivateHoliday).whenever(vacationRepository).update(newPrivateHoliday)

        val vacationSaved = vacationService.updateVacationPeriod(requestVacation, USER, vacation)

        verify(vacationRepository).update(newPrivateHoliday)

        assertEquals(requestVacation.startDate, vacationSaved.startDate)
        assertEquals(requestVacation.endDate, vacationSaved.endDate)
        assertEquals(requestVacation.chargeYear, vacationSaved.chargeYear)
    }

    @Test
    fun `update vacation period when the new corresponding days quantity IS NOT EQUAL to old corresponding days quantity`() {
        val requestVacation = RequestVacation(
            id = VACATION_ID,
            startDate = JAN_SECOND_CURRENT,
            endDate = JAN_SECOND_CURRENT.plusDays(3),
            chargeYear = JAN_SECOND_CURRENT.year,
            description = null
        )
        val vacation = Vacation(
            id = VACATION_ID,
            startDate = JAN_SECOND_CURRENT,
            endDate = JAN_SECOND_CURRENT.plusDays(1),
            state = PENDING,
            userId = USER.id,
            observations = "",
            departmentId = null,
            description = "",
            chargeYear = JAN_SECOND_CURRENT.withDayOfYear(1)
        )

        doReturn(vacation).whenever(vacationRepository).update(eq(vacation))

        doReturn(21)
            .whenever(remainingVacationService).getRemainingVacations(eq(CURRENT_YEAR), eq(USER))

        mockRequestVacation(requestVacation)

        val vacationSaved = vacationService.updateVacationPeriod(requestVacation, USER, vacation)

        assertEquals(requestVacation.startDate, vacationSaved.startDate)
        assertEquals(requestVacation.endDate, vacationSaved.endDate)
        assertEquals(requestVacation.chargeYear, vacationSaved.chargeYear)
    }

    private fun mockRequestVacation(requestVacation: RequestVacation): List<LocalDate> {
        val selectedDays = getSelectedDaysFrom(
            requestVacation.startDate,
            requestVacation.endDate
        )

        doReturn(selectedDays).whenever(remainingVacationService)
            .getRequestedVacationsSelectedYear(eq(requestVacation))

        return selectedDays
    }

    private fun getSelectedDaysFrom(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val dateInterval = DateInterval.of(startDate, endDate)
        val calendar = calendarFactory.create(dateInterval)
        return calendar.getWorkableDays(dateInterval)
    }

    // MUST BE a companion object with @JvmStatic, DO NOT REFACTOR
    // https://blog.oio.de/2018/11/13/how-to-use-junit-5-methodsource-parameterized-tests-with-kotlin/
    private companion object {
        private val USER = createUser()
        private val NOW = LocalDate.now()
        private val CURRENT_YEAR = LocalDate.now().year
        private val LAST_YEAR = LocalDate.now().minusYears(1)
        private val NEXT_YEAR = LocalDate.now().plusYears(1)

        private val LAST_YEAR_FIRST_DAY = LocalDate.of(LAST_YEAR.year, JANUARY, 1)
        private val NEXT_YEAR_LAST_DAY = LocalDate.of(NEXT_YEAR.year, DECEMBER, 31)

        private const val YEAR_2020 = 2020
        private val FIRST_DAY_2020 = LocalDate.of(YEAR_2020, 1, 1)
        private val JAN_TENTH_2020 = LocalDate.of(YEAR_2020, 1, 10)
        private val APR_NINTH_2020 = LocalDate.of(YEAR_2020, 4, 9)
        private val APR_FIRST_2020 = LocalDate.of(YEAR_2020, 4, 1)
        private val APR_THIRTEENTH_2020 = LocalDate.of(YEAR_2020, 4, 30)
        private val DEC_TWEENTYEIGHT_2020 = LocalDate.of(2020, DECEMBER, 28)
        private val JAN_FOURTH_2021 = LocalDate.of(2021, JANUARY, 4)


        private val FIRST_MONDAY = LocalDate.of(CURRENT_YEAR, JANUARY, 1).with(TemporalAdjusters.firstInMonth(MONDAY))
        private val END_DATE = LocalDate.of(YEAR_2020, 1, 31)

        private val FOURTH_FEB_2020 = LocalDate.of(YEAR_2020, 2, 4)
        private val NEW_YEAR_2020 = Holiday(1, "Año nuevo 2020", FIRST_DAY_2020.atStartOfDay())
        private val NEW_YEAR_2021 = Holiday(1, "Año nuevo 2021", LocalDateTime.of(2021, JANUARY, 1, 0, 0))
        private val REYES_2020 = Holiday(2, "Reyes", LocalDate.of(YEAR_2020, 1, 7).atStartOfDay())

        private val holidays2020 = listOf(NEW_YEAR_2020, REYES_2020)
        private val holidays2021 = listOf(NEW_YEAR_2021)

        private val EMPTY_VACATIONS = emptyList<Vacation>()
        private const val VACATION_ID = 10L
        private const val ID_DELETE = 1L
        private val vacations2020 = listOf(
            createVacation(
                startDate = FIRST_DAY_2020,
                endDate = LocalDate.of(YEAR_2020, 1, 10),
                userId = USER.id,
                chargeYear = FIRST_DAY_2020
            ),
            createVacation(
                id = 2,
                startDate = LocalDate.of(YEAR_2020, 2, 3),
                endDate = FOURTH_FEB_2020,
                userId = USER.id,
                chargeYear = FIRST_DAY_2020
            )
        )

        private val NEW_YEAR_CURRENT_DATE =
            LocalDate.of(CURRENT_YEAR, JANUARY, 1).with(TemporalAdjusters.firstInMonth(MONDAY))
                .plusDays(1)

        private val NEW_YEAR_CURRENT_HOLIDAYS = listOf(
            createHoliday(1, "New Year holiday", NEW_YEAR_CURRENT_DATE),
        )

        private val holidaysBetweenLastYearAndCurrent = listOf(
            createHoliday(1, "Holiday 2019", LocalDate.of(LAST_YEAR.year, DECEMBER, 31)),
            createHoliday(2, "Holiday 2020", LocalDate.of(CURRENT_YEAR, JANUARY, 1))
        )
        private val REQUEST_8_DAYS_IN_JANUARY = RequestVacation(
            id = null,
            startDate = FIRST_MONDAY,
            endDate = FIRST_MONDAY.plusDays(10),
            chargeYear = CURRENT_YEAR,
            description = "Lorem ipsum..."
        )
        private val JAN_SECOND_CURRENT =
            LocalDate.of(LocalDate.now().year, JANUARY, 2).with(TemporalAdjusters.firstInMonth(MONDAY))
        private val SEPT_FOURTEENTH_CURRENT =
            LocalDate.of(LocalDate.now().year, SEPTEMBER, 14).with(TemporalAdjusters.firstInMonth(MONDAY))
        private val SEPT_FOURTEENTH_LAST = SEPT_FOURTEENTH_CURRENT.year - 1
        private val SEPT_FOURTEENTH_NEXT = SEPT_FOURTEENTH_CURRENT.year + 1

        private fun createVacation(
            id: Long = 1L,
            startDate: LocalDate,
            endDate: LocalDate,
            state: VacationState = PENDING,
            userId: Long = 1L,
            observations: String = "",
            departmentId: Long? = null,
            description: String = "Dummy description",
            chargeYear: LocalDate = NOW,
        ): Vacation =
            Vacation(
                id,
                startDate,
                endDate,
                state,
                userId,
                observations,
                departmentId,
                description,
                chargeYear,
            )

        private fun createHoliday(
            id: Int = 1,
            description: String = "Fake description",
            day: LocalDate = LocalDate.now(),
        ) = Holiday(id.toLong(), description, LocalDateTime.of(day, LocalTime.MIN))

    }

}
