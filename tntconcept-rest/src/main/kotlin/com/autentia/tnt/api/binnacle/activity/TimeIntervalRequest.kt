package com.autentia.tnt.api.binnacle.activity

import java.time.LocalDateTime

data class TimeIntervalRequest(
    val start: LocalDateTime,
    val end: LocalDateTime,
)