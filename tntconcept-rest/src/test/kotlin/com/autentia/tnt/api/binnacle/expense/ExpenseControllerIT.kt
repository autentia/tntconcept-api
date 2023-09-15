package com.autentia.tnt.api.binnacle.expense

import com.autentia.tnt.api.binnacle.exchangeList
import com.autentia.tnt.api.binnacle.toJson
import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.ExpenseType
import com.autentia.tnt.binnacle.entities.dto.ExpenseFilterDTO
import com.autentia.tnt.binnacle.entities.dto.ExpenseResponseDTO
import com.autentia.tnt.binnacle.usecases.ExpenseByFilterUseCase
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDateTime


@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ExpenseControllerIT {

    @Inject
    @field:Client(value = "/", errorType = String::class)
    private lateinit var httpClient: HttpClient

    private lateinit var client: BlockingHttpClient

    @get:MockBean(ExpenseByFilterUseCase::class)
    internal val expenseByFilterUseCase = mock<ExpenseByFilterUseCase>()

    @BeforeAll
    fun setup() {
        client = httpClient.toBlocking()
    }

    @Test
    fun `get all expenses between the start and end date`() {

        val expenseResponseDTOs = listOf(EXPENSE_RESPONSE_DTO)
        val expenses = listOf(EXPENSE_RESPONSE)

        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    startDate = START_DATE, endDate = END_DATE
                )
            )
        ).thenReturn(expenseResponseDTOs)

        val response = client.exchangeList<ExpenseResponse>(
            HttpRequest.GET(
                "/api/expense?" + "startDate=${START_DATE.toJson()}" + "&endDate=${END_DATE.toJson()}"
            ),
        )

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertEquals(expenses, response.body.get())

    }

    @Test
    fun `get all expenses by status `() {
        val expenseResponseDTOs = listOf(EXPENSE_RESPONSE_DTO)
        val expenses = listOf(EXPENSE_RESPONSE)

        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    state = ApprovalState.PENDING
                )
            )
        ).thenReturn(expenseResponseDTOs)

        val response = client.exchangeList<ExpenseResponse>(
            HttpRequest.GET(
                "/api/expense?" + "state=${ApprovalState.PENDING}"
            ),
        )

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertEquals(expenses, response.body.get())
    }


    @Test
    fun `get all expenses by user `() {
        val expenseResponseDTOs = listOf(EXPENSE_RESPONSE_DTO)
        val expenses = listOf(EXPENSE_RESPONSE)

        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    userId = 1
                )
            )
        ).thenReturn(expenseResponseDTOs)

        val response = client.exchangeList<ExpenseResponse>(
            HttpRequest.GET(
                "/api/expense?" + "userId=1"
            ),
        )

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertEquals(expenses, response.body.get())
    }

    @Test
    fun `get all expenses between the start and end date and user`() {

        val expenseResponseDTOs = listOf(EXPENSE_RESPONSE_DTO)
        val expenses = listOf(EXPENSE_RESPONSE)

        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    startDate = START_DATE, endDate = END_DATE, userId = 1
                )
            )
        ).thenReturn(expenseResponseDTOs)

        val response = client.exchangeList<ExpenseResponse>(
            HttpRequest.GET(
                "/api/expense?" + "startDate=${START_DATE.toJson()}" + "&endDate=${END_DATE.toJson()}" + "&userId=1"
            ),
        )

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertEquals(expenses, response.body.get())

    }

    @Test
    fun `get all expenses by status and user `() {
        val expenseResponseDTOs = listOf(EXPENSE_RESPONSE_DTO)
        val expenses = listOf(EXPENSE_RESPONSE)

        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    state = ApprovalState.PENDING,
                    userId = 1
                )
            )
        ).thenReturn(expenseResponseDTOs)

        val response = client.exchangeList<ExpenseResponse>(
            HttpRequest.GET(
                "/api/expense?" + "state=${ApprovalState.PENDING}" + "&userId=1"
            ),
        )

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertEquals(expenses, response.body.get())
    }

    @Test
    fun `get all expenses between the start and end date and user ans state`() {

        val expenseResponseDTOs = listOf(EXPENSE_RESPONSE_DTO)
        val expenses = listOf(EXPENSE_RESPONSE)

        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    startDate = START_DATE, endDate = END_DATE, userId = 1, state = ApprovalState.PENDING
                )
            )
        ).thenReturn(expenseResponseDTOs)

        val response = client.exchangeList<ExpenseResponse>(
            HttpRequest.GET(
                "/api/expense?" + "startDate=${START_DATE.toJson()}" + "&endDate=${END_DATE.toJson()}" + "&userId=1" + "&state=${ApprovalState.PENDING}"
            ),
        )

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertEquals(expenses, response.body.get())

    }

    @Test
    fun `get all expenses by status and between the start and end date`() {
        val expenseResponseDTOs = listOf(EXPENSE_RESPONSE_DTO)
        val expenses = listOf(EXPENSE_RESPONSE)


        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    startDate = START_DATE, endDate = END_DATE, state = ApprovalState.PENDING
                )
            )
        ).thenReturn(expenseResponseDTOs)

        val response = client.exchangeList<ExpenseResponse>(
            HttpRequest.GET(
                "/api/expense?" + "startDate=${START_DATE.toJson()}" + "&endDate=${END_DATE.toJson()}" + "&state=${ApprovalState.PENDING}"
            ),
        )

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertEquals(expenses, response.body.get())

    }

    @Test
    fun `get all expenses throw a bad request exception if the request has no filter`() {

        whenever(
            expenseByFilterUseCase.find(
                ExpenseFilterDTO(
                    startDate = null, endDate = null, state = null
                )
            )
        ).thenThrow(
            IllegalArgumentException("At least one filter must be provided.")
        )

        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeList<ExpenseResponse>(
                HttpRequest.GET(
                    "/api/expense"
                ),
            )
        }

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.status)
        Assertions.assertEquals("At least one filter must be provided.", ex.message)

    }

    @Test
    fun `get all expenses throw a bad request exception if url is wrong`() {
        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeList<ExpenseResponse>(
                HttpRequest.GET(
                    "/api/expense?" + "startDate=${START_DATE.toJson()}" + "endDate=${END_DATE.toJson()}"
                ))
        }

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.status)
        Assertions.assertEquals("Bad Request", ex.message)
    }

    @Test
    fun `get all expenses throw a bad request exception if start date is wrong format`() {
        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeList<ExpenseResponse>(
                HttpRequest.GET(
                    "/api/expense?" + "startDate=2018-01-10" + "endDate=${END_DATE.toJson()}"
                ))
        }

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.status)
        Assertions.assertEquals("Bad Request", ex.message)
    }

    @Test
    fun `get all expenses throw a bad request exception if end date is wrong format`() {
        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeList<ExpenseResponse>(
                HttpRequest.GET(
                    "/api/expense?" + "endDate=${START_DATE.toJson()}" + "2018-01-10"
                ))
        }

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.status)
        Assertions.assertEquals("Bad Request", ex.message)
    }

    @Test
    fun `get all expenses throw a bad request exception if status does not have the right values`() {
        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeList<ExpenseResponse>(
                HttpRequest.GET(
                    "/api/expense?" + "state=CLOSED"
                ))
        }

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.status)
        Assertions.assertEquals("Bad Request", ex.message)
    }

    private companion object {
        private val START_DATE = LocalDateTime.of(2018, 1, 10, 8, 0)
        private val END_DATE = LocalDateTime.of(2018, 1, 10, 12, 0)

        private val EXPENSE_RESPONSE_DTO = ExpenseResponseDTO(
            1,
            LocalDateTime.of(2018, 1, 10, 8, 0), "EXPENSE",
            BigDecimal(10.00), 1, ApprovalState.PENDING.name,ExpenseType.MARKETING.name, listOf()
        )

        private val EXPENSE_RESPONSE = ExpenseResponse.from(EXPENSE_RESPONSE_DTO)
    }
}