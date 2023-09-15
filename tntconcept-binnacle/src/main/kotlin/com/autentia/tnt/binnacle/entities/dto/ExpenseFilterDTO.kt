package com.autentia.tnt.binnacle.entities.dto

import com.autentia.tnt.binnacle.entities.ApprovalState
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime


@Introspected
data class ExpenseFilterDTO (
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    var state: ApprovalState? = null,
    val userId: Long? = null,
)
