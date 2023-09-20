package com.autentia.tnt.api.binnacle.holiday

import com.autentia.tnt.api.binnacle.exchangeObject
import com.autentia.tnt.api.binnacle.vacation.HolidayDetailsResponse
import com.autentia.tnt.api.binnacle.vacation.HolidayResponse
import com.autentia.tnt.binnacle.entities.dto.HolidayDTO
import com.autentia.tnt.binnacle.entities.dto.HolidayResponseDTO
import com.autentia.tnt.binnacle.usecases.UserHolidaysBetweenDatesUseCase
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class HolidayControllerIT {

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
    fun `get the holidays by year`() {
        val currYear = LocalDate.now().year
        doReturn(HOLIDAY_RESPONSE_DTO).whenever(holidaysBetweenDateForUserUseCase).getHolidays(currYear)

        val response = client.exchangeObject<HolidayResponse>(
            HttpRequest.GET("/api/holiday?year=${currYear}")
        )

        assertEquals(HttpStatus.OK, response.status())
        assertEquals(HOLIDAY_RESPONSE, response.body.get())
    }

    @Test
    fun `get the holidays of current year if no year provided`() {
        doReturn(HOLIDAY_RESPONSE_DTO).whenever(holidaysBetweenDateForUserUseCase).getHolidays(null)

        val response = client.exchangeObject<HolidayResponse>(
            HttpRequest.GET("/api/holiday")
        )

        assertEquals(HttpStatus.OK, response.status())
        assertEquals(HOLIDAY_RESPONSE, response.body.get())
    }

    private companion object {

        private val HOLIDAY_RESPONSE_DTO = HolidayResponseDTO(
            listOf(HolidayDTO(1, "New year", LocalDate.of(LocalDate.now().year, 1, 1)))
        )

        private val HOLIDAY_RESPONSE = HolidayResponse(
            listOf(HolidayDetailsResponse(1, "New year", LocalDate.of(LocalDate.now().year, 1, 1))),
        )
    }

}