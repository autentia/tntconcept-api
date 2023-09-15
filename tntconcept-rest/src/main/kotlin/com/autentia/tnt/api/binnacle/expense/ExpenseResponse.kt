package com.autentia.tnt.api.binnacle.expense

import com.autentia.tnt.binnacle.entities.dto.ExpenseResponseDTO
import java.math.BigDecimal
import java.time.LocalDateTime

data class ExpenseResponse(
    val id: Long,
    val date: LocalDateTime,
    val description: String,
    val amount: BigDecimal,
    val userId: Long,
    val state: String,
    val type: String,
    val documents: List<String>
) {
    companion object {
        fun from(expenseResponseDTO: ExpenseResponseDTO) = ExpenseResponse(
            expenseResponseDTO.id,
            expenseResponseDTO.date,
            expenseResponseDTO.description,
            expenseResponseDTO.amount,
            expenseResponseDTO.userId,
            expenseResponseDTO.state,
            expenseResponseDTO.type,
            expenseResponseDTO.documents
        )
    }
}