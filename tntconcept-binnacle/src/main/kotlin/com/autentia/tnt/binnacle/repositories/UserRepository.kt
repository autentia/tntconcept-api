package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.User
import java.util.Optional


internal interface UserRepository {

    fun findByAuthenticatedUser(): Optional<User>

    fun findById(id: Long): Optional<User>

    fun findByUsername(username: String): User?

    fun findByActiveTrue(): List<User>


}
