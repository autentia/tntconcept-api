package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.Expense
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDateTime
import java.util.*

@Repository
internal interface ExpenseDao:CrudRepository<Expense,Long> {
    @Query("SELECT DISTINCT a FROM Expense a, User u INNER JOIN User ON a.userId = u.id WHERE a.date >= :startDate AND a.date <= :endDate ORDER BY a.date DESC,a.state DESC,u.name ASC")
    fun find(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense>
    @Query("SELECT DISTINCT a FROM Expense a, User u INNER JOIN User ON a.userId = u.id WHERE a.state = :status ORDER BY a.date DESC,a.state DESC,u.name ASC")
    fun find(status:ApprovalState): List<Expense>
    @Query("SELECT a FROM Expense a WHERE a.userId = :userId ORDER BY a.date DESC,a.state DESC")
    fun find(userId:Long): List<Expense>
    @Query("SELECT a FROM Expense a WHERE a.date >= :startDate AND a.date <= :endDate AND a.userId= :userId ORDER BY a.date DESC,a.state DESC")
    fun find(startDate: LocalDateTime, endDate: LocalDateTime,userId:Long): List<Expense>
    @Query("SELECT a FROM Expense a WHERE a.date >= :startDate AND a.date <= :endDate AND a.userId= :userId AND a.state = :status ORDER BY a.date DESC,a.state DESC")
    fun find(startDate: LocalDateTime, endDate: LocalDateTime,userId:Long,status: ApprovalState): List<Expense>
    @Query("SELECT a FROM Expense a WHERE a.id=:id AND a.userId = :userId ORDER BY a.date DESC,a.state DESC")
    fun find(id: Long, userId: Long): Optional<Expense>
    @Query("SELECT a FROM Expense a WHERE a.state = :status AND a.userId=:userId ORDER BY a.date DESC,a.state DESC")
    fun find(status:ApprovalState,userId:Long): List<Expense>
    @Query("SELECT DISTINCT a FROM Expense a, User u INNER JOIN User ON a.userId = u.id WHERE a.date >= :startDate AND a.date <= :endDate AND a.state=:status ORDER BY a.date DESC,a.state DESC,u.name ASC")
    fun find(startDate: LocalDateTime, endDate: LocalDateTime,status: ApprovalState): List<Expense>

}
