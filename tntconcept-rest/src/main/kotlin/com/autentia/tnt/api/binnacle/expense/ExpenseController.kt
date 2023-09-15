package com.autentia.tnt.api.binnacle.expense

import com.autentia.tnt.api.OpenApiTag
import com.autentia.tnt.binnacle.usecases.ExpenseByFilterUseCase
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.validation.Validated
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import javax.validation.Valid

@Controller("/api/expense")
@Validated
@Tag(name = OpenApiTag.EXPENSE)
internal class ExpenseController(private val filterUseCase: ExpenseByFilterUseCase) {

    @Get("{?filterRequest*}")
    @Operation(summary = "Gets expense with specified filters")
    internal fun get (@Valid filterRequest: ExpenseFilterRequest): List<ExpenseResponse>  {
        return filterUseCase.find(filterRequest.toDto()).map { ExpenseResponse.from(it) }
    }
}