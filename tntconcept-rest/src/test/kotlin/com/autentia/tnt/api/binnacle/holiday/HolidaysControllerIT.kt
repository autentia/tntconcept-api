package com.autentia.tnt.api.binnacle.holiday

import com.autentia.tnt.api.binnacle.exchangeObject
import com.autentia.tnt.api.binnacle.vacation.HolidayDetailsResponse
import com.autentia.tnt.api.binnacle.vacation.HolidaysResponse
import com.autentia.tnt.api.binnacle.vacation.VacationResponse
import com.autentia.tnt.binnacle.entities.VacationState
import com.autentia.tnt.binnacle.entities.dto.HolidayDTO
import com.autentia.tnt.binnacle.entities.dto.HolidaysResponseDTO
import com.autentia.tnt.binnacle.entities.dto.VacationDTO
import com.autentia.tnt.binnacle.usecases.UserHolidaysBetweenDatesUseCase
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class HolidaysControllerIT {

    @Inject
    @field:Client("/")
    private lateinit var httpClient: HttpClient

    private lateinit var client: BlockingHttpClient

    @get:MockBean(UserHolidaysBetweenDatesUseCase::class)
    internal val holidaysBetweenDateForUserUseCase = mock<UserHolidaysBetweenDatesUseCase>()


    @BeforeAll
    fun setUp() {
        client = httpClient.toBlocking()
    }

    @Test
    fun `get the holidays by charge year`() {
        doReturn(HOLIDAY_RESPONSE_DTO).whenever(holidaysBetweenDateForUserUseCase).getHolidays(
            START_DATE, START_DATE.plusDays(3L)
        )

        val response = client.exchangeObject<HolidaysResponse>(
            HttpRequest.GET("/api/holidays?startDate=${START_DATE}&endDate=${START_DATE.plusDays(3L)}")
        )

        assertEquals(HttpStatus.OK, response.status())
        assertEquals(HOLIDAY_RESPONSE, response.body.get())
    }

    private companion object {
        private val START_DATE = LocalDate.of(2023, 7, 6)

        val VACATION_DTO = VacationDTO(
            2,
            "Observations",
            "Description",
            VacationState.PENDING,
            START_DATE,
            START_DATE.plusDays(1L),
            listOf(START_DATE),
            START_DATE
        )
        val VACATION_RESPONSE = VacationResponse(
            2,
            "Observations",
            "Description",
            VacationState.PENDING,
            START_DATE,
            START_DATE.plusDays(1L),
            listOf(START_DATE),
            START_DATE
        )

        private val HOLIDAY_RESPONSE_DTO = HolidaysResponseDTO(
            listOf(HolidayDTO(1, "New year", LocalDate.of(LocalDate.now().year, 1, 1))),
            listOf(VACATION_DTO)
        )

        private val HOLIDAY_RESPONSE = HolidaysResponse(
            listOf(HolidayDetailsResponse(1, "New year", LocalDate.of(LocalDate.now().year, 1, 1))),
            listOf(VACATION_RESPONSE)
        )
    }

}