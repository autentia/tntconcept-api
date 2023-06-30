package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.TimeSummaryConverter
import com.autentia.tnt.binnacle.core.domain.TimeSummary
import com.autentia.tnt.binnacle.core.services.TimeSummaryService
import com.autentia.tnt.binnacle.entities.Holiday
import com.autentia.tnt.binnacle.entities.User
import com.autentia.tnt.binnacle.entities.dto.TimeSummaryDTO
import com.autentia.tnt.binnacle.repositories.ActivityRepository
import com.autentia.tnt.binnacle.repositories.UserRepository
import com.autentia.tnt.binnacle.services.*
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month

@Singleton
class UserTimeSummaryUseCase internal constructor(
    private val userRepository: UserRepository,
    private val holidayService: HolidayService,
    private val annualWorkSummaryService: AnnualWorkSummaryService,
    @param:Named("Internal") private val activityRepository: ActivityRepository,
    private val vacationService: VacationService,
    private val myVacationsDetailService: MyVacationsDetailService,
    private val timeSummaryService: TimeSummaryService,
    private val timeSummaryConverter: TimeSummaryConverter
) {
    fun getTimeSummary(date: LocalDate): TimeSummaryDTO {
        val user: User = userRepository.findByAuthenticatedUser()
            .orElseThrow { IllegalStateException("There isn't authenticated user") }
        val timeSummary = getTimeSummary(date, user)
        return timeSummaryConverter.toTimeSummaryDTO(timeSummary)
    }

    fun getTimeSummary(date: LocalDate, user: User): TimeSummary {
        val startYearDate = LocalDate.of(date.year, Month.JANUARY, 1)
        val endYearDate = LocalDate.of(date.year, Month.DECEMBER, 31)


        val annualWorkSummary = annualWorkSummaryService.getAnnualWorkSummary(user, startYearDate.year - 1)
        val holidays: List<Holiday> = holidayService.findAllBetweenDate(startYearDate, endYearDate)
        val holidaysDates = holidays.map { it.date.toLocalDate() }

        val vacationsRequestedThisYear = vacationService.getVacationsBetweenDates(startYearDate, endYearDate, user)
            .filter { it.isRequestedVacation() }

        val vacationDaysEnjoyedThisYear =
            vacationsRequestedThisYear.flatMap { it.days }.filter { it.year == startYearDate.year }

        val vacationsChargedThisYear = vacationService.getVacationsByChargeYear(startYearDate.year, user)

        val correspondingVacations =
            myVacationsDetailService.getCorrespondingVacationDaysSinceHiringDate(user, startYearDate.year)

        val activities = getUserActivitiesBetweenDates(startYearDate, endYearDate, user.id)

        val previousActivities = getUserActivitiesBetweenDates(
            startYearDate.minusYears(1), endYearDate.minusYears(1), user.id
        )

        return timeSummaryService.getTimeSummaryBalance(
            date,
            user,
            annualWorkSummary,
            holidaysDates,
            vacationDaysEnjoyedThisYear,
            vacationsChargedThisYear,
            correspondingVacations,
            activities,
            previousActivities
        )
    }

    private fun getUserActivitiesBetweenDates(startDate: LocalDate, endDate: LocalDate, userId: Long): List<com.autentia.tnt.binnacle.core.domain.Activity> {
        val startDateMinHour = startDate.atTime(LocalTime.MIN)
        val endDateMaxHour = endDate.atTime(LocalTime.MAX)
        return activityRepository.findByUserId(startDateMinHour, endDateMaxHour, userId).map { it.toDomain() }
    }
}
