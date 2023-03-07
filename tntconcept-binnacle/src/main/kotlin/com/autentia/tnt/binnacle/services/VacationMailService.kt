package com.autentia.tnt.binnacle.services

import com.autentia.tnt.AppProperties
import io.micronaut.context.MessageSource
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.Locale

@Singleton
internal class VacationMailService(
    private val mailService: MailService,
    private val messageSource: MessageSource,
    private val appProperties: AppProperties
) {

    fun sendRequestVacationsMail(
        userName: String,
        beginDate: LocalDate,
        finalDate: LocalDate,
        userComment: String,
        locale: Locale
    ) {
        if (!appProperties.mail.enabled) {
            logger.info("Mailing of vacations is disabled")
            return
        }

        val body = messageSource
            .getMessage(
                "mail.request.vacations.template",
                locale,
                userName,
                beginDate.toString(),
                finalDate.toString(),
                userComment
            )
            .orElse(null) ?: error("Cannot find message mail.request.vacations.template")

        val subject = messageSource
            .getMessage("mail.request.vacations.subject", locale, userName)
            .orElse(null) ?: error("Cannot find message mail.request.vacations.subject")

        mailService.send(appProperties.mail.from, appProperties.binnacle.vacationsApprovers, subject, body)
            .onFailure { logger.error("Error sending vacations email", it) }

    }

    private companion object {
        private val logger = LoggerFactory.getLogger(VacationMailService::class.java)
    }

}
