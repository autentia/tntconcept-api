package com.autentia.tnt.api.binnacle.holiday

import com.autentia.tnt.api.OpenApiTag.Companion.HOLIDAY
import com.autentia.tnt.api.binnacle.vacation.HolidayDetailsResponse
import com.autentia.tnt.binnacle.usecases.UserHolidayBetweenDatesUseCase
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Controller("/api/holiday")
@Tag(name = HOLIDAY)
internal class HolidayController(
    private val userHolidayBetweenDatesUseCase: UserHolidayBetweenDatesUseCase
) {

    @Operation(summary = "Retrieves existing holidays given a year")
    @Get
    fun getHolidaysByYear(@QueryValue year: Int?): List<HolidayDetailsResponse> {
        return userHolidayBetweenDatesUseCase.getHolidays(year).map { HolidayDetailsResponse.from(it) }
    }
}
