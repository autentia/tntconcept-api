package com.autentia.tnt.security.application

import io.micronaut.security.authentication.Authentication
import io.micronaut.security.utils.SecurityService

private const val ADMIN_ROLE = "admin"
private const val ACTIVITY_APPROVAL_ROLE = "activity-approval"
private const val BLOCK_PROJECT_ROLE = "project-blocker"
private const val EXPENSE_APPROVAL_ROLE = "expense-approval"

fun SecurityService.checkAuthentication(): Authentication =
    authentication.orElseThrow { IllegalStateException("Required authentication") }

fun SecurityService.checkRole(role: String): Authentication {
    val authentication = checkAuthentication()
    check(authentication.roles.contains(role)) { "Required role" }
    return authentication
}

fun SecurityService.checkActivityApprovalRole(): Authentication {
    return checkRole(ACTIVITY_APPROVAL_ROLE)
}

fun SecurityService.checkBlockProjectsRole(): Authentication {
    return checkRole(BLOCK_PROJECT_ROLE)
}

fun Authentication.isAdmin(): Boolean = roles.contains(ADMIN_ROLE)
private fun Authentication.isActivityApproval(): Boolean = roles.contains(ACTIVITY_APPROVAL_ROLE)

private fun Authentication.isExpenseApproval(): Boolean = roles.contains(EXPENSE_APPROVAL_ROLE)

private fun Authentication.isBlockProject(): Boolean = roles.contains(BLOCK_PROJECT_ROLE)

fun Authentication.canAccessAllUsers() = isAdmin() || isActivityApproval() || isBlockProject()

fun Authentication.canAccessAllActivities() = isAdmin() || isActivityApproval()

fun Authentication.canAccessAllAttachments() = isAdmin() || isActivityApproval()

fun Authentication.canBlockProjects() = isAdmin() || isBlockProject()

fun Authentication.canAccessAllExpense() = isAdmin() || isExpenseApproval()

fun Authentication.id(): Long = name.toLong()
