package com.autentia.tnt.api.binnacle

import com.autentia.tnt.binnacle.usecases.CalendarWorkableDaysUseCase
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.swagger.v3.oas.annotations.Operation
import java.time.LocalDate

@Controller("/api/calendar")
internal class CalendarController(
    private val calendarWorkableDaysUseCase: CalendarWorkableDaysUseCase,
) {

    @Get("/workable-days/count")
    @Operation(summary = "Retrieves workable days within a given period.")
    internal fun getNumberOfWorkableDays(@QueryValue startDate: LocalDate, @QueryValue endDate: LocalDate): Int =
        calendarWorkableDaysUseCase.get(startDate, endDate)

}