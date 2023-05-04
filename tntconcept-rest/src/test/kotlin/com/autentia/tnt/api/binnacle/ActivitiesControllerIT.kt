package com.autentia.tnt.api.binnacle

import com.autentia.tnt.binnacle.entities.dto.*
import com.autentia.tnt.binnacle.exception.ActivityBeforeHiringDateException
import com.autentia.tnt.binnacle.exception.ActivityNotFoundException
import com.autentia.tnt.binnacle.exception.ActivityPeriodClosedException
import com.autentia.tnt.binnacle.exception.OverlapsAnotherTimeException
import com.autentia.tnt.binnacle.exception.ProjectClosedException
import com.autentia.tnt.binnacle.exception.ProjectRoleNotFoundException
import com.autentia.tnt.binnacle.exception.UserPermissionException
import com.autentia.tnt.binnacle.usecases.*
import io.micronaut.http.HttpRequest.DELETE
import io.micronaut.http.HttpRequest.GET
import io.micronaut.http.HttpRequest.POST
import io.micronaut.http.HttpRequest.PUT
import io.micronaut.http.HttpStatus
import io.micronaut.http.HttpStatus.BAD_REQUEST
import io.micronaut.http.HttpStatus.NOT_FOUND
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month.JANUARY

@MicronautTest
@TestInstance(PER_CLASS)
internal class ActivitiesControllerIT {

    @Inject
    @field:Client("/")
    private lateinit var httpClient: HttpClient

    private lateinit var client: BlockingHttpClient

    @get:MockBean(ActivitiesBetweenDateUseCaseOld::class)
    internal val activitiesBetweenDateUseCase = mock<ActivitiesBetweenDateUseCaseOld>()

    @get:MockBean(ActivitiesRetrievalByIdUseCase::class)
    internal val activityRetrievalUseCase = mock<ActivitiesRetrievalByIdUseCase>()

    @get:MockBean(ActivitiesCreationUseCase::class)
    internal val activityCreationUseCase = mock<ActivitiesCreationUseCase>()

    @get:MockBean(ActivitiesUpdateUseCase::class)
    internal val activityUpdateUseCase = mock<ActivitiesUpdateUseCase>()

    @get:MockBean(ActivitiesDeletionUseCase::class)
    internal val activityDeletionUseCase = mock<ActivitiesDeletionUseCase>()

    @get:MockBean(ActivitiesImageRetrievalUseCase::class)
    internal val activityImageRetrievalUseCase = mock<ActivitiesImageRetrievalUseCase>()

    @BeforeAll
    fun setup() {
        client = httpClient.toBlocking()
    }

    @Test
    fun `get all activities between the start and end date`() {
        val startDate = LocalDate.of(2018, JANUARY, 1)
        val endDate = LocalDate.of(2018, JANUARY, 31)
        val activities = listOf(ACTIVITY_DATE_DTO)
        doReturn(activities).whenever(activitiesBetweenDateUseCase).getActivities(startDate, endDate)

        val response = client.exchangeList<ActivityDateDTO>(
            GET("/api/activities?startDate=${startDate.toJson()}&endDate=${endDate.toJson()}"),
        )

        assertEquals(OK, response.status)
        assertEquals(activities, response.body.get())
    }

    @Test
    fun `get activity by id`() {
        doReturn(ACTIVITY_RESPONSE_DTO).whenever(activityRetrievalUseCase).getActivityById(ACTIVITY_RESPONSE_DTO.id)

        val response = client.exchangeObject<ActivitiesResponseDTO>(
            GET("/api/activities/${ACTIVITY_RESPONSE_DTO.id}")
        )

        assertEquals(OK, response.status)
        assertEquals(ACTIVITY_RESPONSE_DTO, response.body.get())
    }

    @Test
    fun `fail if try to get an activity with a non existing id`() {
        val nonExistingId = 8L
        doReturn(null).whenever(activityRetrievalUseCase).getActivityById(nonExistingId)

        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeObject<Any>(
                GET("/api/activities/$nonExistingId"),
            )
        }

        assertEquals(NOT_FOUND, ex.status)
    }

    @Test
    fun `get an image's activity by id`() {
        val userId = ACTIVITY_RESPONSE_DTO.userId
        doReturn(ACTIVITY_IMAGE).whenever(activityImageRetrievalUseCase).getActivityImage(userId)

        val response = client.exchangeObject<String>(
            GET("/api/activities/$userId/image")
        )

        assertEquals(OK, response.status)
        assertEquals(ACTIVITY_IMAGE, response.body())
    }

    @Test
    fun `post a new activity`() {
        doReturn(ACTIVITY_RESPONSE_DTO).whenever(activityCreationUseCase).createActivity(ACTIVITY_REQUEST_BODY_DTO)

        val response = client.exchangeObject<ActivitiesResponseDTO>(
            POST("/api/activities", ACTIVITY_POST_JSON)
        )

        assertEquals(OK, response.status)
        assertEquals(ACTIVITY_RESPONSE_DTO, response.body.get())
    }

    @Test
    fun `fail if try to post activity with too long description`() {
        val tooLongDescriptionJson = ACTIVITY_POST_JSON.replace(
            ACTIVITY_REQUEST_BODY_DTO.description,
            "x".repeat(2049)
        )

        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeObject<Any>(
                POST("/api/activities", tooLongDescriptionJson),
            )
        }

        assertEquals(BAD_REQUEST, ex.status)
    }

    private fun postFailProvider() = arrayOf(
        arrayOf(UserPermissionException(), NOT_FOUND, "RESOURCE_NOT_FOUND"),
        arrayOf(ProjectRoleNotFoundException(1), NOT_FOUND, "RESOURCE_NOT_FOUND"),
        arrayOf(ActivityPeriodClosedException(), BAD_REQUEST, "ACTIVITY_PERIOD_CLOSED"),
        arrayOf(OverlapsAnotherTimeException(), BAD_REQUEST, "ACTIVITY_TIME_OVERLAPS"),
        arrayOf(ProjectClosedException(), BAD_REQUEST, "CLOSED_PROJECT"),
        arrayOf(ActivityBeforeHiringDateException(), BAD_REQUEST, "ACTIVITY_BEFORE_HIRING_DATE")
    )

    @ParameterizedTest
    @MethodSource("postFailProvider")
    fun `fail if try to post an activity and a exception is throw`(
        exception: Exception,
        expectedResponseStatus: HttpStatus,
        expectedErrorCode: String
    ) {
        doThrow(exception).whenever(activityCreationUseCase).createActivity(ACTIVITY_REQUEST_BODY_DTO)

        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeObject<Any>(
                POST("/api/activities", ACTIVITY_POST_JSON),
            )
        }

        assertEquals(expectedResponseStatus, ex.status)
        assertEquals(expectedErrorCode, ex.response.getBody<ErrorResponse>().get().code)
    }

    @Test
    fun `put an activity`() {
        val putActivity = ACTIVITY_REQUEST_BODY_DTO.copy(
            id = ACTIVITY_RESPONSE_DTO.id,
            description = "Updated activity description"
        )
        val updatedActivity = ACTIVITY_RESPONSE_DTO.copy(
            description = putActivity.description
        )
        doReturn(updatedActivity).whenever(activityUpdateUseCase).updateActivity(putActivity)

        val response = client.exchangeObject<ActivitiesResponseDTO>(
            PUT("/api/activities", ACTIVITY_PUT_JSON),
        )

        assertEquals(OK, response.status)
        assertEquals(updatedActivity, response.body.get())
    }

    private fun putFailProvider() = arrayOf(
        arrayOf(UserPermissionException(), NOT_FOUND, "RESOURCE_NOT_FOUND"),
        arrayOf(ActivityNotFoundException(1), NOT_FOUND, "RESOURCE_NOT_FOUND"),
        arrayOf(ProjectRoleNotFoundException(1), NOT_FOUND, "RESOURCE_NOT_FOUND"),
        arrayOf(ActivityPeriodClosedException(), BAD_REQUEST, "ACTIVITY_PERIOD_CLOSED"),
        arrayOf(OverlapsAnotherTimeException(), BAD_REQUEST, "ACTIVITY_TIME_OVERLAPS"),
        arrayOf(ProjectClosedException(), BAD_REQUEST, "CLOSED_PROJECT"),
        arrayOf(ActivityBeforeHiringDateException(), BAD_REQUEST, "ACTIVITY_BEFORE_HIRING_DATE")
    )

    @ParameterizedTest
    @MethodSource("putFailProvider")
    fun `fail if try to put an activity and exception is throw`(
        exception: Exception,
        expectedResponseStatus: HttpStatus,
        expectedErrorCode: String
    ) {
        doThrow(exception).whenever(activityUpdateUseCase).updateActivity(ACTIVITY_REQUEST_BODY_DTO)

        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeObject<Any>(
                PUT("/api/activities", ACTIVITY_POST_JSON),
            )
        }

        assertEquals(expectedResponseStatus, ex.status)
        assertEquals(expectedErrorCode, ex.response.getBody<ErrorResponse>().get().code)
    }

    @Test
    fun `delete an activity`() {
        val activityIdToDelete = 14L

        val response = client.exchange<Any, Any>(
            DELETE("/api/activities/$activityIdToDelete")
        )

        assertEquals(OK, response.status)
        verify(activityDeletionUseCase).deleteActivityById(activityIdToDelete)
    }

    private fun deleteFailProvider() = arrayOf(
        arrayOf(UserPermissionException(), NOT_FOUND, "RESOURCE_NOT_FOUND"),
        arrayOf(ActivityNotFoundException(1), NOT_FOUND, "RESOURCE_NOT_FOUND"),
        arrayOf(ActivityPeriodClosedException(), BAD_REQUEST, "ACTIVITY_PERIOD_CLOSED")
    )

    @ParameterizedTest
    @MethodSource("deleteFailProvider")
    fun `fail if try to delete an activity and exception is throw`(
        exception: Exception,
        expectedResponseStatus: HttpStatus,
        expectedErrorCode: String
    ) {
        doThrow(exception).whenever(activityDeletionUseCase).deleteActivityById(ACTIVITY_RESPONSE_DTO.id)

        val ex = assertThrows<HttpClientResponseException> {
            client.exchangeObject<Unit>(
                DELETE("/api/activities/${ACTIVITY_RESPONSE_DTO.id}"),
            )
        }

        assertEquals(expectedResponseStatus, ex.status)
        assertEquals(expectedErrorCode, ex.response.getBody<ErrorResponse>().get().code)
    }

    private companion object {
        private val START_DATE = LocalDateTime.of(2018, JANUARY, 10, 8, 0)

        private val ACTIVITY_REQUEST_BODY_DTO = ActivitiesRequestBodyDTO(
            null,
            START_DATE,
            4 * 60,
            "Activity description",
            true,
            3,
            false,
            null
        )

        private val ACTIVITY_POST_JSON = """
            {
                "startDate": "${ACTIVITY_REQUEST_BODY_DTO.startDate.toJson()}",
                "duration": ${ACTIVITY_REQUEST_BODY_DTO.duration},
                "description": "${ACTIVITY_REQUEST_BODY_DTO.description}",
                "billable": ${ACTIVITY_REQUEST_BODY_DTO.billable},
                "projectRoleId": ${ACTIVITY_REQUEST_BODY_DTO.projectRoleId}
            }
        """.trimIndent()

        private val ACTIVITY_RESPONSE_DTO = ActivitiesResponseDTO(
            42,
            ACTIVITY_REQUEST_BODY_DTO.startDate,
            ACTIVITY_REQUEST_BODY_DTO.duration,
            ACTIVITY_REQUEST_BODY_DTO.description,
            ProjectRoleResponseDTOOld(ACTIVITY_REQUEST_BODY_DTO.projectRoleId, "role", true),
            2,
            ACTIVITY_REQUEST_BODY_DTO.billable,
            OrganizationResponseDTO(6, "organization"),
            ProjectResponseDTO(5, "project", true, true, 1L),
            ACTIVITY_REQUEST_BODY_DTO.hasImage,
        )

        private val ACTIVITY_PUT_JSON = """
            {
                "id": ${ACTIVITY_RESPONSE_DTO.id},
                "startDate": "${ACTIVITY_RESPONSE_DTO.startDate.toJson()}",
                "duration": ${ACTIVITY_RESPONSE_DTO.duration},
                "description": "Updated activity description",
                "billable": ${ACTIVITY_RESPONSE_DTO.billable},
                "projectRoleId": ${ACTIVITY_RESPONSE_DTO.projectRole.id}
            }
        """.trimIndent()

        private val ACTIVITY_DATE_DTO = ActivityDateDTO(
            ACTIVITY_REQUEST_BODY_DTO.startDate.toLocalDate(),
            ACTIVITY_REQUEST_BODY_DTO.duration,
            listOf(ACTIVITY_RESPONSE_DTO)
        )

        private val ACTIVITY_IMAGE = "base64image"
    }

}