package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.repositories.ProjectRepository
import io.archimedesfw.commons.time.ClockUtils
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Singleton
class AutoBlockProjectUseCase internal constructor(
    private val calendarWorkableDaysUseCase: CalendarWorkableDaysUseCase,
    internal val projectRepository: ProjectRepository
) {


    fun blockOpenProjectsOnSecondDayOfMonth(){

        val secondDay = isSecondWorkableDayOfMonth()
        if(secondDay) projectRepository.blockOpenProjects(getLastDayOfPreviousMonth())

    }

    private fun getLastDayOfPreviousMonth(): LocalDate {
        return ClockUtils.nowUtc().toLocalDate().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
    }

    private fun isSecondWorkableDayOfMonth(): Boolean {
        val today = ClockUtils.nowUtc().toLocalDate()
        val firstDayOfMonth = ClockUtils.nowUtc().toLocalDate().withDayOfMonth(1)
        val workableDays = calendarWorkableDaysUseCase.get(firstDayOfMonth, today)

        return workableDays == 2
    }
}