package com.autentia.tnt.api.binnacle.activity

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.dto.ActivityFilterDTO
import io.micronaut.core.annotation.Introspected
import java.time.LocalDate

@Introspected
data class ActivityFilterRequest(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val approvalState: ApprovalState? = null,
    val organizationId: Long? = null,
    val projectId: Long? = null,
    val roleId: Long? = null,
    val userId: Long? = null,
) {
    fun toDto(): ActivityFilterDTO =
        ActivityFilterDTO(
            startDate,
            endDate,
            approvalState,
            organizationId,
            projectId,
            roleId,
            userId,
        )
}
