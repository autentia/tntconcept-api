package com.autentia.tnt.binnacle.core.domain

import com.autentia.tnt.binnacle.entities.RequireEvidence
import java.time.LocalDateTime

data class ProjectRoleRecent (
    val id: Long,
    val name: String,
    val projectName: String,
    val organizationName: String,
    val projectBillable: Boolean,
    val projectOpen: Boolean,
    val date: LocalDateTime,
    val requireEvidence: RequireEvidence,
)
