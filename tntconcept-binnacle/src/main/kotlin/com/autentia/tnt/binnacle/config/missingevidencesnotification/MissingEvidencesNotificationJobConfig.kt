package com.autentia.tnt.binnacle.config.missingevidencesnotification

import com.autentia.tnt.AppProperties
import io.micronaut.context.annotation.Context
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.TaskScheduler
import jakarta.inject.Named


@Context
internal class MissingEvidencesNotificationJobConfig(
    private val appProperties: AppProperties,
    @Named(TaskExecutors.SCHEDULED) taskScheduler: TaskScheduler
) {
    init{
        if (appProperties.binnacle.missingEvidencesNotification.onceCronExpression != null){
            taskScheduler.schedule(appProperties.binnacle.missingEvidencesNotification.onceCronExpression) {
                println("HEllO")
            }
        }
    }
}