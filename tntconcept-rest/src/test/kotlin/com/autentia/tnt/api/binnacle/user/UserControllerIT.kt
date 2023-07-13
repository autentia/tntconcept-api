package com.autentia.tnt.api.binnacle.user

import com.autentia.tnt.api.binnacle.exchangeList
import com.autentia.tnt.binnacle.entities.Role
import com.autentia.tnt.binnacle.entities.User
import com.autentia.tnt.binnacle.entities.WorkingAgreement
import com.autentia.tnt.binnacle.entities.dto.UserInfoResponseDTO
import com.autentia.tnt.binnacle.entities.dto.UserResponseDTO
import com.autentia.tnt.binnacle.usecases.FindUserInfoUseCase
import com.autentia.tnt.binnacle.usecases.UsersRetrievalUseCase
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus.*
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
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate

@MicronautTest
@TestInstance(PER_CLASS)
internal class UserControllerIT {

    @Inject
    @field:Client("/")
    private lateinit var httpClient: HttpClient

    private lateinit var client: BlockingHttpClient

    @get:MockBean(FindUserInfoUseCase::class)
    internal val findUserInfoUseCase = mock<FindUserInfoUseCase>()

    @get:MockBean(UsersRetrievalUseCase::class)
    internal val usersRetrievalUseCase = mock<UsersRetrievalUseCase>()

    @BeforeAll
    fun setup() {
        client = httpClient.toBlocking()
    }

    @Test
    fun `get logged user`() {
        val user = User(
            1L,
            "username",
            "password",
            2L,
            "name",
            "photoUrl",
            dayDuration = 24,
            WorkingAgreement(3L, emptySet()),
            null,
            LocalDate.now(),
            "email",
            Role(4, "role"),
            true
        )
        val roles = listOf("user")

        val userInfoResponseDTO = UserInfoResponseDTO(user.id, user.username, user.hiringDate, roles)
        whenever(findUserInfoUseCase.find()).thenReturn(userInfoResponseDTO)

        val request = HttpRequest.GET<Any>("/api/user/me")

        val response = client.exchange(request, UserInfoResponse::class.java)

        assertEquals(200, response.status.code)
        assertEquals(UserInfoResponse.from(userInfoResponseDTO), response.body.get())
    }

    @Test
    fun `get all active users`() {
        whenever(usersRetrievalUseCase.getAllActiveUsers()).thenReturn(listOf(USER_RESPONSE_DTO))

        val response = client.exchangeList<UserResponse>(
            HttpRequest.GET("/api/user"),
        )

        assertEquals(OK, response.status)
        assertEquals(listOf(UserResponse.from(USER_RESPONSE_DTO)), response.body.get())
    }

    private companion object {
        private val USER_RESPONSE_DTO = UserResponseDTO(
            1L,
            "username",
            "Name surname",
        )
    }

}