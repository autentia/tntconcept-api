package com.autentia.tnt.binnacle.converters

import com.autentia.tnt.binnacle.entities.Expense
import com.autentia.tnt.binnacle.entities.dto.ExpenseResponseDTO
import jakarta.inject.Singleton

@Singleton
class ExpenseResponseConverter {
    fun toResponseDTO(expense: Expense) = ExpenseResponseDTO(
        id = expense.id!!,
        date = expense.date,
        description = expense.description,
        amount = expense.amount,
        userId = expense.userId,
        state = expense.state.name,
        type = expense.type.name,
        documents = expense.attachments.map { it.id.toString() }
    )
}
