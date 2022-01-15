package com.logreposit.renogyrover.services

import com.logreposit.renogyrover.communication.renogy.RenogyClient
import com.logreposit.renogyrover.services.logreposit.LogrepositApiService
import com.logreposit.renogyrover.utils.logger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduledReportingService(
    private val renogyClient: RenogyClient,
    private val logrepositApiService: LogrepositApiService
) {
    val logger = logger()

    @Scheduled(initialDelay = 5000, fixedDelayString = "\${logreposit.scrapeIntervalInMillis}")
    fun readAndReport() {
        logger.info("Reading data from Renogy Rover controller and publishing it to the Logreposit API ...")

        logrepositApiService.pushData(renogyClient.getRamData())
    }
}
