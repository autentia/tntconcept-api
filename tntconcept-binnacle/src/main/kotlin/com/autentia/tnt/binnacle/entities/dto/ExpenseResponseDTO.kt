package com.autentia.tnt.binnacle.entities.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ExpenseResponseDTO(
    val id: Long,
    val date: LocalDateTime,
    val description: String,
    val amount: BigDecimal,
    val userId:Long,
    val state: String,
    val type: String,
    val documents: List<String>
)
