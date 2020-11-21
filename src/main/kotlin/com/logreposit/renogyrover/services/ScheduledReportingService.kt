package com.logreposit.renogyrover.services

import com.logreposit.renogyrover.services.logreposit.LogrepositApiService
import com.logreposit.renogyrover.communication.renogy.RenogyClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduledReportingService(
        private val renogyClient: RenogyClient,
        private val logrepositApiService: LogrepositApiService
) {
    @Scheduled(initialDelay = 5000, fixedDelayString = "\${logreposit.scrapeIntervalInMillis}")
    fun readAndReport() {
        val ramData = renogyClient.getRamData()

        println(ramData)
    }
}
