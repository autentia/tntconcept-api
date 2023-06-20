package com.autentia.tnt.api.binnacle.activity

import com.autentia.tnt.api.OpenApiTag.Companion.ACTIVITY
import com.autentia.tnt.api.binnacle.ErrorResponse
import com.autentia.tnt.api.binnacle.ErrorResponseMaxHoursLimit
import com.autentia.tnt.api.binnacle.ErrorValues
import com.autentia.tnt.binnacle.entities.dto.ActivityFilterDTO
import com.autentia.tnt.binnacle.entities.dto.ActivityResponseDTO
import com.autentia.tnt.binnacle.exception.*
import com.autentia.tnt.binnacle.usecases.*
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate
import java.util.*
import javax.validation.Valid

@Controller("/api/activity")
@Validated
@Tag(name = ACTIVITY)
internal class ActivityController(
    private val activityByFilterUserCase: ActivitiesByFilterUseCase,
    private val activityRetrievalUseCase: ActivityRetrievalByIdUseCase,
    private val activityCreationUseCase: ActivityCreationUseCase,
    private val activityUpdateUseCase: ActivityUpdateUseCase,
    private val activityDeletionUseCase: ActivityDeletionUseCase,
    private val activityEvidenceRetrievalUseCase: ActivityEvidenceRetrievalUseCase,
    private val activitiesSummaryUseCase: ActivitiesSummaryUseCase,
    private val activityApprovalUseCase: ActivityApprovalUseCase,
    private val activityConverter: ActivityConverter,
) {

    @Get("{?activityFilterDTO*}")
    @Operation(summary = "Gets activities with specified filters")
    internal fun get(activityFilterDTO: ActivityFilterDTO): List<ActivityResponseDTO> =
        activityByFilterUserCase.getActivities(activityFilterDTO)


    @Get("/{id}")
    @Operation(summary = "Gets an activity by its id.")
    internal fun get(id: Long): ActivityResponseDTO? = activityRetrievalUseCase.getActivityById(id)

    @Get("/{id}/evidence")
    @Operation(summary = "Retrieves an activity evidence by the activity id.")
    internal fun getEvidenceByActivityId(id: Long): String =
        activityEvidenceRetrievalUseCase.getActivityEvidenceByActivityId(id).getDataUrl()

    @Post
    @Operation(summary = "Creates a new activity")
    internal fun post(
        @Body @Valid activityRequest: ActivityRequest, @Parameter(hidden = true) locale: Locale,
    ): ActivityResponseDTO =
        activityCreationUseCase.createActivity(activityConverter.convertTo(activityRequest), locale)

    @Put
    @Operation(summary = "Updates an existing activity")
    internal fun put(
        @Valid @Body activityRequest: ActivityRequest, @Parameter(hidden = true) locale: Locale,
    ): ActivityResponseDTO = activityUpdateUseCase.updateActivity(activityConverter.convertTo(activityRequest), locale)


    @Delete("/{id}")
    @Operation(summary = "Deletes an activity by its id.")
    internal fun delete(id: Long) = activityDeletionUseCase.deleteActivityById(id)


    @Get("/summary")
    @Operation(summary = "Gets activities summary between two dates")
    internal fun summary(startDate: LocalDate, endDate: LocalDate) =
        activitiesSummaryUseCase.getActivitiesSummary(startDate, endDate)

    @Post("/{id}/approve")
    @Operation(summary = "Approve an existing activity by id.")
    internal fun approve(
        @Body id: Long, locale: Locale,
    ): ActivityResponseDTO = activityApprovalUseCase.approveActivity(id, locale)

    @Error
    internal fun onTimeIntervalException(request: HttpRequest<*>, e: TimeIntervalException) =
        HttpResponse.badRequest(ErrorResponse("INVALID_DATE_RANGE", e.message))

    @Error
    internal fun onOverlapAnotherActivityTimeException(request: HttpRequest<*>, e: OverlapsAnotherTimeException) =
        HttpResponse.badRequest(ErrorResponse("ACTIVITY_TIME_OVERLAPS", e.message))

    @Error
    internal fun onReachedMaxImputableHoursForRole(request: HttpRequest<*>, e: MaxHoursPerRoleException) =
        HttpResponse.badRequest(
            ErrorResponseMaxHoursLimit(
                "MAX_REGISTRABLE_HOURS_LIMIT_EXCEEDED",
                e.message,
                ErrorValues(e.maxAllowedHours, e.remainingHours, e.year)
            )
        )

    @Error
    internal fun onProjectClosedException(request: HttpRequest<*>, e: ProjectClosedException) =
        HttpResponse.badRequest(ErrorResponse("CLOSED_PROJECT", e.message))

    @Error
    internal fun onProjectBlockedException(request: HttpRequest<*>, e: ProjectBlockedException) =
        HttpResponse.badRequest(
            ErrorResponseBlockedProject(
                "BLOCKED_PROJECT",
                e.message,
                ErrorResponseBlockedProject.ErrorValues(e.blockedDate)
            )
        )

    @Error
    internal fun onActivityPeriodClosedException(request: HttpRequest<*>, e: ActivityPeriodClosedException) =
        HttpResponse.badRequest(ErrorResponse("ACTIVITY_PERIOD_CLOSED", e.message))

    @Error
    internal fun onActivityBeforeHiringDateException(request: HttpRequest<*>, e: ActivityBeforeHiringDateException) =
        HttpResponse.badRequest(ErrorResponse("ACTIVITY_BEFORE_HIRING_DATE", e.message))

    @Error
    internal fun onNoEvidenceInActivityException(request: HttpRequest<*>, e: NoEvidenceInActivityException) =
        HttpResponse.badRequest(ErrorResponse("No evidence", e.message))

    @Error
    internal fun onActivityAlreadyApproved(request: HttpRequest<*>, e: InvalidActivityApprovalStateException) =
        HttpResponse.status<HttpStatus>(HttpStatus.CONFLICT)
            .body(ErrorResponse("INVALID_ACTIVITY_APPROVAL_STATE", e.message))
}
