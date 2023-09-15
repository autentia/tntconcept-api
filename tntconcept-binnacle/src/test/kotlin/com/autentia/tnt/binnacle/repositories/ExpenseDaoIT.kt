package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.AttachmentInfo
import com.autentia.tnt.binnacle.entities.Expense
import com.autentia.tnt.binnacle.entities.ExpenseType
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.Month
import java.util.*

@MicronautTest()
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ExpenseDaoIT {

    @Inject
    private lateinit var expenseDaoSut: ExpenseDao


    @Test
    fun `should find all expense by date range sort by latest date, "Pending" status and by employee name in alphabetical ascending order`() {
        var actualExpense: List<Expense> =
            expenseDaoSut.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 22, 0, 0, 0))
        Assertions.assertTrue(actualExpense.size == 6, "The result of the search by dates is not as expected")

        Assertions.assertTrue(actualExpense[0].id == 4L)
        Assertions.assertTrue(actualExpense[1].id == 6L)
        Assertions.assertTrue(actualExpense[2].id == 3L)
        Assertions.assertTrue(actualExpense[3].id == 5L)
        Assertions.assertTrue(actualExpense[4].id == 2L)
        Assertions.assertTrue(actualExpense[5].id == 1L)



        actualExpense =
            expenseDaoSut.find(LocalDateTime.of(2023, 8, 22, 0, 0, 0), LocalDateTime.of(2023, 8, 22, 0, 0, 0))
        Assertions.assertTrue(actualExpense.isEmpty(), "The result of the search by dates is not as expected")
    }

    @Test
    fun `should find expense without attachment by id`() {
        val expectedExpense = Optional.of(
            Expense(
                1,
                1,
                LocalDateTime.of(2023, Month.AUGUST, 17, 0, 0, 0),
                "Expense for tests",
                BigDecimal("10.00"),
                ExpenseType.MARKETING,
                ApprovalState.PENDING
            )
        )
        val actualExpense = expenseDaoSut.findById(1)
        Assertions.assertEquals(expectedExpense, actualExpense)
    }

    @Test
    fun `should find expense with attachment by id`() {
        val expectedExpense = Optional.of(
            Expense(
                2,
                1,
                LocalDateTime.of(2023, Month.AUGUST, 18, 0, 0, 0),
                "Expense for tests",
                BigDecimal("10.00"),
                ExpenseType.MARKETING,
                ApprovalState.ACCEPTED,
                attachments = listOf(
                    AttachmentInfo(
                        id = UUID.fromString("4d3cbe3f-369f-11ee-99c2-0242ac180003"),
                        userId = 11,
                        path = "path/to/test/file",
                        fileName = "testFile,jpg",
                        mimeType = "image/jpeg",
                        uploadDate = LocalDateTime.of(2023, 8, 17, 0, 0, 0),
                        isTemporary = true
                    )
                )
            )
        )
        val actualExpense = expenseDaoSut.findById(2)
        Assertions.assertEquals(expectedExpense, actualExpense)
    }


    @Test
    fun `should find all expense by status sort by latest date, "Pending" status and by employee name in alphabetical ascending order`() {
        val actualExpense: List<Expense> = expenseDaoSut.find(ApprovalState.PENDING)
        Assertions.assertTrue(actualExpense.size == 3, "The result of the search by status is not as expected")
        Assertions.assertTrue(actualExpense[0].id == 3L)
        Assertions.assertTrue(actualExpense[1].id == 5L)
        Assertions.assertTrue(actualExpense[2].id == 1L)
    }

    @Test
    fun `should find all expense by user`() {
        val actualExpense: List<Expense> = expenseDaoSut.find(1)
        Assertions.assertTrue(actualExpense.size == 4, "The result of the search by status is not as expected")
    }


    @Test
    fun `should find all expense by date range and user`() {
        var actualExpense: List<Expense> =
            expenseDaoSut.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 1)
        Assertions.assertTrue(actualExpense.size == 2, "The result of the search by dates and user is not as expected")
        actualExpense =
            expenseDaoSut.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 10)
        Assertions.assertTrue(actualExpense.isEmpty(), "The result of the search by dates and user is not as expected")
        actualExpense =
            expenseDaoSut.find(LocalDateTime.of(2023, 8, 18, 0, 0, 0), LocalDateTime.of(2023, 8, 19, 0, 0, 0), 2)
        Assertions.assertTrue(actualExpense.isEmpty(), "The result of the search by dates and user is not as expected")
    }

    @Test
    fun `should find all expense by date range and status and user`() {
        var actualExpense: List<Expense> =
            expenseDaoSut.find(
                LocalDateTime.of(2023, 8, 16, 0, 0, 0),
                LocalDateTime.of(2023, 8, 18, 0, 0, 0),
                1,
                ApprovalState.PENDING
            )
        Assertions.assertTrue(
            actualExpense.size == 1,
            "The result of the search by dates and user and status is not as expected"
        )
        actualExpense = expenseDaoSut.find(
            LocalDateTime.of(2023, 8, 22, 0, 0, 0),
            LocalDateTime.of(2023, 8, 23, 0, 0, 0),
            1,
            ApprovalState.ACCEPTED
        )
        Assertions.assertTrue(
            actualExpense.isEmpty(),
            "The result of the search by dates and user and status is not as expected"
        )
    }

    @Test
    fun `should find all expense by id and user`() {
        val actualExpense: Optional<Expense> = expenseDaoSut.find(1, 1)
        Assertions.assertFalse(
            actualExpense.isEmpty,
            "The result of the search by id and userId is not as expected"
        )
    }

    @Test
    fun `should find all expense by status and user`() {
        val actualExpense: List<Expense> = expenseDaoSut.find(ApprovalState.PENDING, 1)
        Assertions.assertTrue(
            actualExpense.size == 2,
            "The result of the search by status and userId is not as expected"
        )
    }

    @Test
    fun `should find all expense by date range and status order by latest date, "Pending" status and by employee name in alphabetical ascending order`() {
        var actualExpense: List<Expense> =
            expenseDaoSut.find(
                LocalDateTime.of(2023, 8, 16, 0, 0, 0),
                LocalDateTime.of(2023, 8, 18, 0, 0, 0),
                ApprovalState.PENDING
            )
        Assertions.assertTrue(
            actualExpense.size == 1,
            "The result of the search by dates and status is not as expected"
        )
    }


    @Test
    fun `should save a new expense`() {
        val expectedExpenseWithoutAttachment = Expense(
            7,
            1,
            LocalDateTime.of(2023, Month.AUGUST, 17, 0, 0, 0),
            "Expense for tests",
            BigDecimal("11.00"),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )
        val actualExpenseWithoutAttachment: Expense = expenseDaoSut.save(
            Expense(
                null,
                1,
                LocalDateTime.of(2023, Month.AUGUST, 17, 0, 0, 0),
                "Expense for tests",
                BigDecimal("11.00"),
                ExpenseType.OPERATION,
                ApprovalState.PENDING
            )
        )

        Assertions.assertEquals(expectedExpenseWithoutAttachment, actualExpenseWithoutAttachment)

        val expectedExpense = Expense(
            8,
            1,
            LocalDateTime.of(2023, Month.AUGUST, 17, 0, 0, 0),
            "Expense for tests",
            BigDecimal("11.00"),
            ExpenseType.OPERATION,
            ApprovalState.PENDING,
            attachments = listOf(
                AttachmentInfo(
                    id = UUID.fromString("4d3cbe3f-369f-11ee-99c2-0242ac180003"),
                    userId = 11,
                    path = "path/to/test/file",
                    fileName = "testFile,jpg",
                    mimeType = "image/jpeg",
                    uploadDate = LocalDateTime.of(2023, 8, 17, 0, 0, 0),
                    isTemporary = true
                )
            )
        )
        val actualExpense: Expense = expenseDaoSut.save(
            Expense(
                null,
                1,
                LocalDateTime.of(2023, Month.AUGUST, 17, 0, 0, 0),
                "Expense for tests",
                BigDecimal("11.00"),
                ExpenseType.OPERATION,
                ApprovalState.PENDING,
                attachments = listOf(
                    AttachmentInfo(
                        id = UUID.fromString("4d3cbe3f-369f-11ee-99c2-0242ac180003"),
                        userId = 11,
                        path = "path/to/test/file",
                        fileName = "testFile,jpg",
                        mimeType = "image/jpeg",
                        uploadDate = LocalDateTime.of(2023, 8, 17, 0, 0, 0),
                        isTemporary = true
                    )
                )
            )
        )

        Assertions.assertEquals(expectedExpense, actualExpense)
    }

    @Test
    fun `should update an exist expense without attachment`() {
        val originalExpense = expenseDaoSut.findById(1)
        expenseDaoSut.update(originalExpense.get().copy(amount = BigDecimal("1000.50")))
        Assertions.assertEquals(BigDecimal("1000.50"), originalExpense.get().amount)
    }

    @Test
    fun `should update an exist expense with attachment`() {
        val originalExpense = expenseDaoSut.findById(2)
        expenseDaoSut.update(originalExpense.get().copy(amount = BigDecimal("2000.50")))
        Assertions.assertEquals(BigDecimal("2000.50"), originalExpense.get().amount)
    }

    @Test
    fun `should delete an exist expense without attachment`() {
        expenseDaoSut.deleteById(1)
        val originalExpense = expenseDaoSut.findById(1)
        Assertions.assertFalse(originalExpense.isPresent)
    }

    @Test
    fun `should delete an exist expense with attachment`() {
        expenseDaoSut.deleteById(2)
        val originalExpense = expenseDaoSut.findById(2)
        Assertions.assertFalse(originalExpense.isPresent)
    }

}