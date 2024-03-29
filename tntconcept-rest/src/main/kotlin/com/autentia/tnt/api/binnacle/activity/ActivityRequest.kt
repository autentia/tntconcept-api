package com.autentia.tnt.api.binnacle.activity

import com.autentia.tnt.binnacle.entities.dto.ActivityRequestDTO
import com.autentia.tnt.binnacle.entities.dto.EvidenceDTO
import com.autentia.tnt.binnacle.entities.dto.TimeIntervalRequestDTO
import io.micronaut.core.annotation.Introspected
import javax.annotation.Nullable
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

@Introspected
data class ActivityRequest(
    val id: Long? = null,
    val interval: TimeIntervalRequest,
    @field:Size(max = 2048, message = "Description must not exceed 2048 characters")
    val description: String,
    val billable: Boolean,
    val projectRoleId: Long,
    val hasEvidences: Boolean,
    @field:Pattern(
        regexp = "^data:[^,]+;base64,.+$", message = "String format should be data:<mediatype>;base64,<data>"
    )
    @field:Nullable
    val evidence: String? = null,
) {
    fun toDto(): ActivityRequestDTO = ActivityRequestDTO(
        id,
        TimeIntervalRequestDTO(interval.start, interval.end),
        description,
        billable,
        projectRoleId,
        hasEvidences,
        evidence = if (evidence != null) EvidenceDTO.from(evidence) else null
    )
}