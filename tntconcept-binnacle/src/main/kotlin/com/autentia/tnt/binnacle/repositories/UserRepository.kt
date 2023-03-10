package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.User
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
internal interface UserRepository : CrudRepository<User, Long> {

    fun findByUsername(username: String): User?

    fun findByActiveTrue(): List<User>

}
