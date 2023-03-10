package com.autentia.tnt.binnacle.services

import com.autentia.tnt.binnacle.config.createUser
import com.autentia.tnt.binnacle.converters.ActivityRequestBodyConverter
import com.autentia.tnt.binnacle.converters.ActivityResponseConverter
import com.autentia.tnt.binnacle.converters.OrganizationResponseConverter
import com.autentia.tnt.binnacle.converters.ProjectResponseConverter
import com.autentia.tnt.binnacle.converters.ProjectRoleResponseConverter
import com.autentia.tnt.binnacle.core.domain.ActivityRequestBody
import com.autentia.tnt.binnacle.core.domain.ActivityResponse
import com.autentia.tnt.binnacle.core.domain.ActivityTimeOnly
import com.autentia.tnt.binnacle.entities.Activity
import com.autentia.tnt.binnacle.entities.Organization
import com.autentia.tnt.binnacle.entities.Project
import com.autentia.tnt.binnacle.entities.ProjectRole
import com.autentia.tnt.binnacle.exception.ActivityNotFoundException
import com.autentia.tnt.binnacle.repositories.ActivityRepository
import com.autentia.tnt.binnacle.repositories.ProjectRoleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.verify
import org.mockito.BDDMockito.verifyNoInteractions
import org.mockito.BDDMockito.willDoNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import java.util.Optional

internal class ActivityServiceTest {

    private val activityRepository = mock<ActivityRepository>()
    private val projectRoleRepository = mock<ProjectRoleRepository>()
    private val activityImageService = mock<ActivityImageService>()
    private val activityRequestBodyConverter = ActivityRequestBodyConverter()
    private val activityResponseConverter = ActivityResponseConverter(
        OrganizationResponseConverter(),
        ProjectResponseConverter(),
        ProjectRoleResponseConverter()
    )

    private val activityService = ActivityService(
        activityRepository,
        projectRoleRepository,
        activityImageService,
        activityRequestBodyConverter,
        activityResponseConverter
    )

    init {
        doReturn(Optional.of(projectRole))
            .whenever(projectRoleRepository).findById(projectRole.id)
    }

    private val activityWithImageToSave = activityRequestBodyConverter.mapActivityRequestBodyToActivity(
        activityWithImageRequest,
        projectRole,
        USER
    )
    private val activityWithoutImageToSave = activityRequestBodyConverter.mapActivityRequestBodyToActivity(
        activityWithoutImageRequest,
        projectRole,
        USER
    )

    private val activityWithImageSaved = activityWithImageToSave.copy(id = 101, insertDate = Date())

    private val activityWithoutImageSaved = activityWithoutImageToSave.copy(id = 100L, insertDate = Date())


    @Test
    fun `get activity by id`() {
        doReturn(Optional.of(activityWithoutImageSaved)).whenever(activityRepository)
            .findById(activityWithoutImageSaved.id as Long)

        val actual = activityService.getActivityById(activityWithoutImageSaved.id as Long)

        assertEquals(activityWithoutImageSaved, actual)
    }

    @Test
    fun `fail when the activity was not found by id`() {
        assertThrows<ActivityNotFoundException> {
            activityService.getActivityById(notFoundActivityId)
        }
    }

    @Test
    fun `get activities between start and end date`() {
        val startDate = LocalDate.of(2019, 1, 1)
        val endDate = LocalDate.of(2019, 1, 31)
        val userId = 1L

        doReturn(listOf(activityWithoutImageSaved))
            .whenever(activityRepository).getActivitiesBetweenDate(
                startDate.atTime(LocalTime.MIN),
                endDate.atTime(23, 59, 59),
                userId
            )

        val actual = activityService.getActivitiesBetweenDates(startDate, endDate, userId)

        assertEquals(
            listOf(
                ActivityResponse(
                    activityWithoutImageSaved.id as Long,
                    activityWithoutImageSaved.startDate,
                    activityWithoutImageSaved.duration,
                    activityWithoutImageSaved.description,
                    activityWithoutImageSaved.projectRole,
                    activityWithoutImageSaved.userId,
                    activityWithoutImageSaved.billable,
                    activityWithoutImageSaved.projectRole.project.organization,
                    activityWithoutImageSaved.projectRole.project,
                    false,
                )
            ),
            actual
        )
    }

    @Test
    fun `get worked minutes between start and end date`() {
        val startDate = LocalDate.of(2019, 1, 1)
        val endDate = LocalDate.of(2019, 1, 31)
        val userId = 2L
        val activityTimeOnly = ActivityTimeOnly(
            activityWithoutImageSaved.startDate,
            activityWithoutImageSaved.duration,
            activityWithoutImageSaved.projectRole.id
        )

        doReturn(listOf(activityTimeOnly))
            .whenever(activityRepository).workedMinutesBetweenDate(
                startDate.atTime(LocalTime.MIN),
                endDate.atTime(23, 59, 59),
                userId
            )

        val actual = activityService.workedMinutesBetweenDates(startDate, endDate, userId)

        assertEquals(listOf(activityTimeOnly), actual)
    }

    @Test
    fun `create activity`() {
        doReturn(activityWithoutImageSaved).whenever(activityRepository).save(activityWithoutImageToSave)

        val result = activityService.createActivity(activityWithoutImageRequest, USER)

        assertEquals(activityWithoutImageSaved, result)
        verifyNoInteractions(activityImageService)
    }

    @Test
    fun `create activity and store image file`() {
        doReturn(activityWithImageSaved).whenever(activityRepository).save(activityWithImageToSave)

        val result = activityService.createActivity(activityWithImageRequest, USER)

        assertEquals(activityWithImageSaved, result)
        verify(activityImageService).storeActivityImage(
            activityWithImageSaved.id!!,
            activityWithImageRequest.imageFile,
            activityWithImageSaved.insertDate!!
        )
    }

    @Test
    fun `update activity`() {
        val activityRequest = ActivityRequestBody(
            activityWithoutImageSaved.id,
            TODAY_NOON,
            120,
            "Description...",
            false,
            projectRole.id,
            false
        )

        doReturn(Optional.of(activityWithoutImageSaved))
            .whenever(activityRepository).findById(activityWithoutImageSaved.id!!)

        val savedActivity = mock(Activity::class.java)

        val activityToSave = activityRequestBodyConverter.mapActivityRequestBodyToActivity(
            activityRequest,
            projectRole,
            USER,
            activityWithoutImageSaved.insertDate
        )
        doReturn(savedActivity).whenever(activityRepository).update(activityToSave)

        val result = activityService.updateActivity(activityRequest, USER)

        assertEquals(savedActivity, result)
        verifyNoInteractions(activityImageService)
    }

    @Test
    fun `update activity and update the stored image`() {
        val activityId = 90L
        val activityRequest = ActivityRequestBody(
            activityId,
            LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
            120,
            "Description...",
            false,
            projectRole.id,
            true,
            "Base64 format..."
        )

        val oldActivity = mock(Activity::class.java)
        val oldActivityInsertDate = Date()
        doReturn(oldActivityInsertDate).whenever(oldActivity).insertDate

        doReturn(Optional.of(oldActivity)).whenever(activityRepository).findById(activityId)

        // Store the new image in the same old activity path
        willDoNothing().given(activityImageService)
            .storeActivityImage(activityRequest.id!!, activityRequest.imageFile, oldActivityInsertDate)

        val activityToReturn = mock(Activity::class.java)

        given(
            activityRepository.update(
                activityRequestBodyConverter.mapActivityRequestBodyToActivity(
                    activityRequest,
                    projectRole,
                    USER,
                    oldActivityInsertDate
                )
            )
        ).willReturn(activityToReturn)

        val result = activityService.updateActivity(activityRequest, USER)

        assertThat(result).isEqualTo(activityToReturn)
        verify(activityImageService).storeActivityImage(
            activityRequest.id!!,
            activityRequest.imageFile,
            oldActivityInsertDate
        )
    }

    @Test
    fun `update activity and delete the stored image`() {
        val activityId = 90L
        val activityRequest = ActivityRequestBody(
            activityId,
            LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
            120,
            "Description...",
            false,
            projectRole.id,
            false
        )

        val oldActivity = mock(Activity::class.java)
        // The old activity has an image but the new activity body does not
        val oldActivityHasImage = true
        val oldActivityInsertDate = Date()
        given(oldActivity.hasImage).willReturn(oldActivityHasImage)
        given(oldActivity.insertDate).willReturn(oldActivityInsertDate)
        given(activityRepository.findById(activityId)).willReturn(Optional.of(oldActivity))

        // Delete the old activity image
        given(activityImageService.deleteActivityImage(activityId, oldActivityInsertDate)).willReturn(true)

        val savedActivity = mock(Activity::class.java)

        given(
            activityRepository.update(
                activityRequestBodyConverter.mapActivityRequestBodyToActivity(
                    activityRequest,
                    projectRole,
                    USER,
                    oldActivityInsertDate
                )
            )
        ).willReturn(savedActivity)

        val result = activityService.updateActivity(activityRequest, USER)

        assertThat(result).isEqualTo(savedActivity)
        verify(activityImageService).deleteActivityImage(activityId, oldActivityInsertDate)
    }

    @Test
    fun `delete activity by id`() {
        doReturn(Optional.of(activityWithoutImageSaved))
            .whenever(activityRepository).findById(activityWithoutImageSaved.id!!)

        activityService.deleteActivityById(activityWithoutImageSaved.id as Long)

        verify(activityRepository).deleteById(activityWithoutImageSaved.id!!)
        verifyNoInteractions(activityImageService)
    }

    @Test
    fun `delete activity by id and its image`() {
        doReturn(Optional.of(activityWithImageSaved))
            .whenever(activityRepository).findById(activityWithImageSaved.id!!)

        activityService.deleteActivityById(activityWithImageSaved.id!!)

        verify(activityRepository).deleteById(activityWithImageSaved.id!!)
        verify(activityImageService).deleteActivityImage(
            activityWithImageSaved.id!!,
            activityWithImageSaved.insertDate!!
        )
    }

    private companion object{
        private val USER = createUser()

        private val organization = Organization(1L, "Autentia", emptyList())
        private val project = Project(1L, "Back-end developers", true, false, organization, emptyList())
        private val projectRole = ProjectRole(10, "Kotlin developer", false, project, 0)

        private val TODAY_NOON = LocalDateTime.of(LocalDate.now(), LocalTime.NOON)

        private const val notFoundActivityId = 1L

        private val activityWithoutImageRequest = ActivityRequestBody(
            null,
            LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
            60,
            "Dummy description",
            false,
            projectRole.id,
            false
        )

        private val activityWithImageRequest = ActivityRequestBody(
            null,
            LocalDateTime.of(LocalDate.now(), LocalTime.NOON),
            120,
            "Description...",
            false,
            projectRole.id,
            true,
            "Base64 format..."
        )
    }

}
