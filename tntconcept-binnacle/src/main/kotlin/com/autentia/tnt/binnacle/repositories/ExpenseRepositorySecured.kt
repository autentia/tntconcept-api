package com.autentia.tnt.binnacle.repositories

import com.autentia.tnt.binnacle.entities.ApprovalState
import com.autentia.tnt.binnacle.entities.Expense
import com.autentia.tnt.binnacle.exception.UserPermissionException
import com.autentia.tnt.security.application.canAccessAllExpense
import com.autentia.tnt.security.application.checkAuthentication
import com.autentia.tnt.security.application.id
import io.micronaut.security.utils.SecurityService
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.util.*

@Singleton
internal class ExpenseRepositorySecured(
    private val expenseDao: ExpenseDao,
    private val securityService: SecurityService
) : ExpenseRepository {
    override fun findById(id: Long): Optional<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense()) {
            expenseDao.findById(id)
        } else {
            expenseDao.find(id, authentication.id())
        }
    }

    override fun find(startDate: LocalDateTime, endDate: LocalDateTime): List<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense()) {
            expenseDao.find(startDate, endDate)
        } else {
            expenseDao.find(startDate, endDate, authentication.id())
        }
    }

    override fun find(status: ApprovalState): List<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense()) {
            expenseDao.find(status)
        } else {
            expenseDao.find(status, authentication.id())
        }
    }

    override fun find(status: ApprovalState,userId: Long): List<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense() || userId == authentication.id()) {
            expenseDao.find(status, userId)
        } else throw UserPermissionException("the received user does not match the identified user")
    }

    override fun find(startDate: LocalDateTime, endDate: LocalDateTime, status: ApprovalState): List<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense()) {
            expenseDao.find(startDate, endDate, status)
        } else {
            expenseDao.find(startDate, endDate, authentication.id(), status)
        }
    }

    override fun find(userId: Long): List<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense() || userId == authentication.id()) {
            expenseDao.find(userId)
        } else throw UserPermissionException("the received user does not match the identified user")
    }

    override fun find(startDate: LocalDateTime, endDate: LocalDateTime, userId: Long): List<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense() || userId == authentication.id()) {
            expenseDao.find(startDate, endDate, userId)
        } else throw UserPermissionException("the received user does not match the identified user")
    }

    override fun find(
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        userId: Long,
        status: ApprovalState
    ): List<Expense> {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense() || userId == authentication.id()) {
            expenseDao.find(startDate, endDate, userId,status)
        } else throw UserPermissionException("the received user does not match the identified user")
    }

    override fun save(expense: Expense): Expense {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense() || expense.userId == authentication.id()) {
            expenseDao.save(expense)
        } else throw UserPermissionException("the received user does not match the identified user")
    }

    override fun update(expense: Expense): Expense {
        val authentication = securityService.checkAuthentication()
        return if (authentication.canAccessAllExpense() || expense.userId == authentication.id()) {
            expenseDao.update(expense)
        } else throw UserPermissionException("the received user does not match the identified user")
    }

    override fun deleteById(id: Long) {
        val authentication = securityService.checkAuthentication()
        val expenseToDelete = expenseDao.findById(id).orElseThrow()
        return if (authentication.canAccessAllExpense() || expenseToDelete.userId == authentication.id()) {
            expenseDao.deleteById(id)
        } else throw UserPermissionException("the received user does not match the identified user")
    }


}
