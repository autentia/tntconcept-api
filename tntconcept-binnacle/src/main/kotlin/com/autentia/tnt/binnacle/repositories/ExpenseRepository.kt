package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.Expense
import java.time.LocalDateTime
import java.util.*

internal interface ExpenseRepository {
    fun findById(id: Long):Optional<Expense>
    fun find(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense>
    fun find(status: ApprovalState): List<Expense>
    fun find(startDate: LocalDateTime, endDate: LocalDateTime,status: ApprovalState): List<Expense>
    fun find(userId: Long): List<Expense>
    fun find(status: ApprovalState,userId: Long): List<Expense>
    fun find(startDate: LocalDateTime, endDate: LocalDateTime, userId: Long): List<Expense>
    fun find(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        userId: Long,
        status: ApprovalState
    ): List<Expense>

    fun save(expense: Expense): Expense

    fun update(expense: Expense): Expense

    fun deleteById(id: Long)

}
