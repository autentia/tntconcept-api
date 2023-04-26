package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.config.createUser
import com.autentia.tnt.binnacle.converters.UserResponseConverter
import com.autentia.tnt.binnacle.entities.dto.UserResponseDTO
import com.autentia.tnt.binnacle.services.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.time.DurationUnit

internal class UsersRetrievalUseCaseTest {
    private val userService = mock<UserService>()

    private val usersRetrievalUseCase = UsersRetrievalUseCase(userService, UserResponseConverter())

    @Test
    fun `should return the list of users`() {
        whenever(userService.findAll()).thenReturn(listOf(createUser()))

        val actual = usersRetrievalUseCase.getAllUsers()

        assertEquals(listOf(userResponseDTO), actual)
    }

    private companion object {
        val userResponseDTO =
            UserResponseDTO(
                createUser().id,
                createUser().username,
                createUser().departmentId,
                createUser().name,
                createUser().photoUrl,
                dayDuration = createUser().dayDuration,
                createUser().agreement,
                createUser().getAnnualWorkingHoursByYear(2023).toInt(DurationUnit.MINUTES),
                createUser().hiringDate,
                createUser().email,
                createUser().role.name,
            )
    }
}