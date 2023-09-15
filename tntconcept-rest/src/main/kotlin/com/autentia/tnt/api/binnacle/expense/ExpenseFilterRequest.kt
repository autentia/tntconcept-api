package com.autentia.tnt.api.binnacle.expense

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.dto.ExpenseFilterDTO
import io.micronaut.core.annotation.Introspected
import java.time.LocalDateTime
import javax.validation.constraints.Pattern

@Introspected
data class ExpenseFilterRequest(
    @field:Pattern(
        regexp = """^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$""", message = "Date format should be data:yyyy-mm-dd HH:mm:ss"
    )
    val startDate: LocalDateTime? = null,
    @field:Pattern(
        regexp = """^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}$""", message = "Date format should be data:yyyy-mm-dd HH:mm:ss"
    )
    val endDate: LocalDateTime? = null,
    @Pattern(regexp = "PENDING|ACCEPTED", flags = [Pattern.Flag.CASE_INSENSITIVE])
    var state: ApprovalState? = null,

    val userId: Long? = null,
) {
    fun toDto(): ExpenseFilterDTO = ExpenseFilterDTO(startDate, endDate, state, userId)
}