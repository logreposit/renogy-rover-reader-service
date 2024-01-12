package com.logreposit.renogyrover.services

import com.logreposit.renogyrover.communication.renogy.RenogyClient
import com.logreposit.renogyrover.communication.renogy.RenogyRamData
import com.logreposit.renogyrover.services.logreposit.LogrepositApiService
import com.logreposit.renogyrover.services.logreposit.SharedTestData.sampleRamData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever

class ScheduledReportingServiceTests {

    private val renogyClient: RenogyClient = mock()
    private val logrepositApiService: LogrepositApiService = mock()
    private val renogyRamDataArgumentCaptor = argumentCaptor<RenogyRamData>()

    @Test
    fun `test readAndReport expect data read from renogy client and pushed to logreposit api`() {
        val scheduledReportingService = ScheduledReportingService(
            renogyClient,
            logrepositApiService
        )

        val ramData = sampleRamData()

        whenever(renogyClient.getRamData()).thenReturn(ramData)

        scheduledReportingService.readAndReport()

        verify(renogyClient, times(1)).getRamData()
        verify(logrepositApiService, times(1)).pushData(
            renogyRamDataArgumentCaptor.capture()
        )

        assertThat(renogyRamDataArgumentCaptor.allValues).hasSize(1)
        assertThat(renogyRamDataArgumentCaptor.firstValue).isSameAs(ramData)
    }
}
