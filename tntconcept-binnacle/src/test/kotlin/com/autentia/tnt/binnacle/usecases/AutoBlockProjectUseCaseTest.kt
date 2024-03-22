package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.repositories.ProjectRepository
import io.archimedesfw.commons.time.test.ClockTestUtils
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import java.time.LocalDate
import java.time.LocalDateTime

class AutoBlockProjectUseCaseTest() {

    internal val projectRepository = mock<ProjectRepository>()
    private val calendarWorkableDaysUseCase = mock<CalendarWorkableDaysUseCase>()
    private val autoBlockProjectUseCase = AutoBlockProjectUseCase(calendarWorkableDaysUseCase,projectRepository)
    @Test
    fun `should not block open projects because it is not the second workable day of the month`() {
        val mockNow = LocalDateTime.of(2024,3,20,0,0)//normal day
        `when`(calendarWorkableDaysUseCase.get(mockNow.withDayOfMonth(1).toLocalDate(),mockNow.toLocalDate())).thenReturn(14)
        ClockTestUtils.runWithFixed(mockNow)
        {
            autoBlockProjectUseCase.blockOpenProjectsOnSecondDayOfMonth()
        }
        verifyNoInteractions(projectRepository)

    }

    @Test
    fun `should not block open projects because it is the first day of the month`() {
        val mockNow = LocalDateTime.of(2024,4,1,0,0)//normal first day of month
        `when`(calendarWorkableDaysUseCase.get(mockNow.withDayOfMonth(1).toLocalDate(), mockNow.toLocalDate())).thenReturn(1)
        ClockTestUtils.runWithFixed(mockNow)
        {
            autoBlockProjectUseCase.blockOpenProjectsOnSecondDayOfMonth()
        }
        verifyNoInteractions(projectRepository)

    }
    @Test
    fun `should block open projects with date 2024-3-31 because it is the second day of the month and also the second workable day`() {
        val mockNow = LocalDateTime.of(2024,4,2,0,0)//normal second workable day of month
        `when`(calendarWorkableDaysUseCase.get(mockNow.withDayOfMonth(1).toLocalDate(),mockNow.toLocalDate())).thenReturn(2)
        ClockTestUtils.runWithFixed(mockNow)
        {
            autoBlockProjectUseCase.blockOpenProjectsOnSecondDayOfMonth()
        }
        verify(projectRepository, times(1)).blockOpenProjects(anyOrNull())
        verify(projectRepository).blockOpenProjects(LocalDate.of(2024,3,31))
    }
}