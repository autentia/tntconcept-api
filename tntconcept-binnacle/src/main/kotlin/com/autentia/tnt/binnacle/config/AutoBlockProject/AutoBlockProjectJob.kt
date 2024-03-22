package com.autentia.tnt.binnacle.config.AutoBlockProject

import com.autentia.tnt.AppProperties
import com.autentia.tnt.binnacle.usecases.AutoBlockProjectUseCase
import io.micronaut.context.annotation.Context
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.TaskScheduler
import jakarta.inject.Named

@Context
internal class AutoBlockProjectJob(
    val autoBlockProjectUseCase: AutoBlockProjectUseCase,
    private val appProperties: AppProperties,
    @Named(TaskExecutors.SCHEDULED) taskScheduler: TaskScheduler
) {
    init {
        if (appProperties.binnacle.autoBlockProject.cronExpression != null) {
            taskScheduler.schedule(appProperties.binnacle.autoBlockProject.cronExpression) {
                autoBlockProjectUseCase.blockOpenProjectsOnSecondDayOfMonth()
            }
        }
    }

}