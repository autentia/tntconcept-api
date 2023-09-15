package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.Expense
import com.autentia.tnt.binnacle.entities.ExpenseType
import com.autentia.tnt.binnacle.exception.UserPermissionException
import io.micronaut.security.authentication.ClientAuthentication
import io.micronaut.security.utils.SecurityService
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class ExpenseRepositorySecuredTest {
    private val expenseDao = mock<ExpenseDao>()
    private val securityService = mock<SecurityService>()
    private val expenseRepositorySecured = ExpenseRepositorySecured(expenseDao, securityService)

    @Test
    fun `find with id filter and permission access all return expense for all user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.findById(1)
        verify(expenseDao).findById(1)
    }

    @Test
    fun `find with id filter and without permission access all return expense for logged user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.findById(1)
        verify(expenseDao).find(1, 1)
    }

    @Test
    fun `find with date range and permission access all return expense for all user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0))
        verify(expenseDao).find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0))
    }

    @Test
    fun `find with date range and without permission access all return expense for logged user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0))
        verify(expenseDao).find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 1)
    }

    @Test
    fun `find with date range ans status and permission access all return expense for all user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0),ApprovalState.PENDING)
        verify(expenseDao).find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0),ApprovalState.PENDING)
    }

    @Test
    fun `find with date range and status and qithout permission access all return expense for logged user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0),ApprovalState.PENDING)
        verify(expenseDao).find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 1,ApprovalState.PENDING)
    }

    @Test
    fun `find with status and permission access all return expense for all user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.find(ApprovalState.PENDING)
        verify(expenseDao).find(ApprovalState.PENDING)
    }


    @Test
    fun `find with status and user with permission access all return all expense for user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.find(ApprovalState.PENDING,1)
        verify(expenseDao).find(ApprovalState.PENDING,1)
    }

    @Test
    fun `find with status and user without permission access all return all expense for user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.find(ApprovalState.PENDING,1)
        verify(expenseDao).find(ApprovalState.PENDING,1)
    }

    @Test
    fun `find with status and user without permission access and without logged return UserPermissionException`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        assertThrows(UserPermissionException::class.java) {
            expenseRepositorySecured.find(ApprovalState.PENDING,2)
        }
        verify(expenseDao, times(0)).find(ApprovalState.PENDING,2)
    }




    @Test
    fun `find with status and without permission access all return expense for logged user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.find(ApprovalState.PENDING)
        verify(expenseDao).find(ApprovalState.PENDING, 1)
    }

    @Test
    fun `find with userId and permission access all return all expense for user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.find(1)
        verify(expenseDao).find(1)
    }

    @Test
    fun `find with userId and without permission access all return all expense for logged user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.find(1)
        verify(expenseDao).find(1)
    }

    @Test()
    fun `find with userId and without permission and without logged return UserPermissionException`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        assertThrows(UserPermissionException::class.java) {
            expenseRepositorySecured.find(2)
        }
        verify(expenseDao, times(0)).find(2)
    }


    @Test
    fun `find with date range and userId and permission access all return all expense for user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 1)
        verify(expenseDao).find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 1)
    }

    @Test
    fun `find with date range and userId and without permission access all return all expense for logged user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 1)
        verify(expenseDao).find(LocalDateTime.of(2023, 8, 16, 0, 0, 0), LocalDateTime.of(2023, 8, 18, 0, 0, 0), 1)
    }

    @Test
    fun `find with date range and userId and without permission access all and without logged return UserPermissionException`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        assertThrows(UserPermissionException::class.java) {
            expenseRepositorySecured.find(
                LocalDateTime.of(2023, 8, 16, 0, 0, 0),
                LocalDateTime.of(2023, 8, 18, 0, 0, 0),
                2
            )
        }
        verify(expenseDao, times(0)).find(
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            LocalDateTime.of(2023, 8, 18, 0, 0, 0),
            2
        )
    }

    @Test
    fun `find with date range and userId and status and permission access all return all expense for user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        expenseRepositorySecured.find(
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            LocalDateTime.of(2023, 8, 18, 0, 0, 0),
            1,
            ApprovalState.PENDING
        )
        verify(expenseDao).find(
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            LocalDateTime.of(2023, 8, 18, 0, 0, 0),
            1,
            ApprovalState.PENDING
        )
    }


    @Test
    fun `find with date range and userId and status and without permission access all return all expense for logged user`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        expenseRepositorySecured.find(
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            LocalDateTime.of(2023, 8, 18, 0, 0, 0),
            1,
            ApprovalState.PENDING
        )
        verify(expenseDao).find(
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            LocalDateTime.of(2023, 8, 18, 0, 0, 0),
            1,
            ApprovalState.PENDING
        )
    }

    @Test
    fun `find with date range and userId and status and without permission access all and without logged return UserPermissionException`() {
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        assertThrows(UserPermissionException::class.java) {
            expenseRepositorySecured.find(
                LocalDateTime.of(2023, 8, 16, 0, 0, 0),
                LocalDateTime.of(2023, 8, 18, 0, 0, 0),
                2,
                ApprovalState.PENDING
            )
        }
        verify(expenseDao, times(0)).find(
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            LocalDateTime.of(2023, 8, 18, 0, 0, 0),
            2,
            ApprovalState.PENDING
        )
    }

    @Test
    fun `save with permission access all return persist new expense`() {
        val expenseToSave = Expense(
            null,
            1,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )
        val expenseExpected = expenseToSave.copy(id = 1)
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        whenever(expenseDao.save(expenseToSave)).thenReturn(expenseExpected)

        expenseRepositorySecured.save(expenseToSave)
        verify(expenseDao).save(expenseToSave)
    }

    @Test
    fun `save without permission access all and logged user return persist new expense`() {
        val expenseToSave = Expense(
            null,
            1,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )
        val expenseExpected = expenseToSave.copy(id = 1)
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        whenever(expenseDao.save(expenseToSave)).thenReturn(expenseExpected)

        expenseRepositorySecured.save(expenseToSave)
        verify(expenseDao).save(expenseToSave)
    }

    @Test
    fun `save without permission access all and without logged user return UserPermissionException`() {
        val expenseToSave = Expense(
            null,
            10,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )
        val expenseExpected = expenseToSave.copy(id = 1)
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        whenever(expenseDao.save(expenseToSave)).thenReturn(expenseExpected)
        assertThrows(UserPermissionException::class.java) {
            expenseRepositorySecured.save(expenseToSave)
        }

        verify(expenseDao, times(0)).save(expenseToSave)
    }
    @Test
    fun `update with permission access all return update expense`() {
        val expenseToUpdate = Expense(
            1,
            1,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )
        val expenseExpected = expenseToUpdate.copy(id = 1)
        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        whenever(expenseDao.update(expenseToUpdate)).thenReturn(expenseExpected)

        expenseRepositorySecured.update(expenseToUpdate)
        verify(expenseDao).update(expenseToUpdate)
    }
    @Test
    fun `update without permission access all and logged user return updated expense`() {
        val expenseToUpdate = Expense(
            1,
            1,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )
        val expenseExpected = expenseToUpdate.copy(id = 1)
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        whenever(expenseDao.update(expenseToUpdate)).thenReturn(expenseExpected)
        expenseRepositorySecured.update(expenseToUpdate)
        verify(expenseDao).update(expenseToUpdate)
    }
    @Test
    fun `update without permission access all and without logged user return UserPermissionException`() {
        val expenseToUpdate = Expense(
            1,
            10,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )
        val expenseExpected = expenseToUpdate.copy(id = 1)
        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        whenever(expenseDao.save(expenseToUpdate)).thenReturn(expenseExpected)
        assertThrows(UserPermissionException::class.java) {
            expenseRepositorySecured.update(expenseToUpdate)
        }

        verify(expenseDao, times(0)).update(expenseToUpdate)
    }


    @Test
    fun `delete with permission access all remove a exist expense`() {
        val expenseToDelete = Expense(
            1,
            1,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )

        whenever(securityService.authentication).thenReturn(Optional.of(adminRolesAuth))
        whenever(expenseDao.findById(1)).thenReturn(Optional.of(expenseToDelete))
        doNothing().whenever(expenseDao).delete(expenseToDelete)

        expenseToDelete.id?.let { expenseRepositorySecured.deleteById(it) }
        verify(expenseDao).deleteById(expenseToDelete.id)
    }
    @Test
    fun `delete without permission access all and logged user remove a exist expense`() {
        val expenseToDelete = Expense(
            1,
            1,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )

        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        whenever(expenseDao.findById(1)).thenReturn(Optional.of(expenseToDelete))
        doNothing().whenever(expenseDao).delete(expenseToDelete)
        expenseToDelete.id?.let { expenseRepositorySecured.deleteById(it) }
        verify(expenseDao).deleteById(expenseToDelete.id)
    }
    @Test
    fun `delete without permission access all and without logged user return UserPermissionException`() {
        val expenseToDelete = Expense(
            1,
            10,
            LocalDateTime.of(2023, 8, 16, 0, 0, 0),
            "test",
            BigDecimal(10),
            ExpenseType.OPERATION,
            ApprovalState.PENDING
        )

        whenever(securityService.authentication).thenReturn(Optional.of(emptyRolesAuth))
        whenever(expenseDao.findById(1)).thenReturn(Optional.of(expenseToDelete))
        doNothing().whenever(expenseDao).delete(expenseToDelete)

        assertThrows(UserPermissionException::class.java) {
            expenseToDelete.id?.let { expenseRepositorySecured.deleteById(it) }
        }

        verify(expenseDao, times(0)).deleteById(expenseToDelete.id)
    }


    private companion object {
        private val emptyRolesAuth =
            ClientAuthentication("1", mapOf("roles" to listOf("user")))
        private val adminRolesAuth =
            ClientAuthentication("2", mapOf("roles" to listOf("admin")))
    }
}