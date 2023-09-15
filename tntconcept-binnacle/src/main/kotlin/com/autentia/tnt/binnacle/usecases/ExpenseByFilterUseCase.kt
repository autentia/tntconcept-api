package com.autentia.tnt.binnacle.usecases

import com.autentia.tnt.binnacle.converters.ExpenseResponseConverter
import com.autentia.tnt.binnacle.entities.Expense
import com.autentia.tnt.binnacle.entities.dto.ExpenseFilterDTO
import com.autentia.tnt.binnacle.entities.dto.ExpenseResponseDTO
import com.autentia.tnt.binnacle.repositories.ExpenseRepository
import io.micronaut.transaction.annotation.ReadOnly
import jakarta.inject.Singleton
import javax.transaction.Transactional

@Singleton
class ExpenseByFilterUseCase internal constructor(
    private val repository: ExpenseRepository,
    private val responseConverter: ExpenseResponseConverter
) {

    @Transactional
    @ReadOnly
    fun find(filter: ExpenseFilterDTO): List<ExpenseResponseDTO> {
        val expenses = filterExpenses(filter)
        return expenses.map { responseConverter.toResponseDTO(it) }
    }

    private fun filterExpenses(filter: ExpenseFilterDTO): List<Expense> {
        return when {
            filter.userId != null -> {
                if (filter.startDate != null && filter.endDate != null) {
                    if (filter.state != null) {
                        repository.find(
                            filter.startDate,
                            filter.endDate,
                            filter.userId,
                            filter.state!!
                        )
                    } else {
                        repository.find(filter.startDate, filter.endDate, filter.userId)
                    }
                } else {
                    if (filter.state != null) {
                        repository.find(filter.state!!, filter.userId)
                    } else {
                        repository.find(filter.userId)
                    }
                }
            }

            filter.startDate != null && filter.endDate != null -> {
                if (filter.state != null) {
                    repository.find(filter.startDate, filter.endDate, filter.state!!)
                } else {
                    repository.find(filter.startDate, filter.endDate)
                }
            }

            filter.state != null -> {
                repository.find(filter.state!!)
            }

            else -> {
                throw IllegalArgumentException("At least one filter must be provided.")
            }
        }
    }

}
