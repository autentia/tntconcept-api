package com.autentia.tnt.binnacle.converters

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.AttachmentInfo
import com.autentia.tnt.binnacle.entities.Expense
import com.autentia.tnt.binnacle.entities.ExpenseType
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID

class ExpenseResponseConverterTest {
    private val converter: ExpenseResponseConverter = ExpenseResponseConverter()

    @Test
    fun toResponseDTO() {
        val dto = converter.toResponseDTO(
            Expense(
                1,
                1,
                LocalDateTime.of(2023, 8, 23, 0, 0, 0),
                "expense",
                BigDecimal(10.0),
                ExpenseType.MARKETING,
                ApprovalState.PENDING,
                attachments = listOf(
                    AttachmentInfo(
                        UUID.randomUUID(),
                        1,
                        "/tmp",
                        "nameFile",
                        "jpeg",
                        LocalDateTime.of(2023, 8, 23, 0, 0, 0),
                        true
                    )
                )
            )
        )

        assertEquals(1L,dto.id)
        assertEquals(LocalDateTime.of(2023, 8, 23, 0, 0, 0),dto.date)
        assertEquals("expense",dto.description)
        assertEquals(BigDecimal(10.0),dto.amount)
        assertEquals(1L,dto.userId)
        assertEquals("PENDING",dto.state)
        assertEquals("MARKETING",dto.type)
        assertEquals(1,dto.documents.size)

    }
}