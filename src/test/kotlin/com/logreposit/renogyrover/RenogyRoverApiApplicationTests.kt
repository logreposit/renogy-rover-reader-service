package com.logreposit.renogyrover

import com.logreposit.renogyrover.configuration.LogrepositConfiguration
import com.logreposit.renogyrover.configuration.RenogyConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RenogyRoverApiApplicationTests {
    @Autowired
    private lateinit var logrepositConfiguration: LogrepositConfiguration

    @Autowired
    private lateinit var renogyConfiguration: RenogyConfiguration

    @Test
    fun contextLoads() {
    }

    @Test
    fun `test logreposit configuration initialization expect values from properties file`() {
        assertThat(logrepositConfiguration.apiBaseUrl).isEqualTo("https://api.logreposit.com")
        assertThat(logrepositConfiguration.deviceToken).isEqualTo("INVALID")
        assertThat(logrepositConfiguration.scrapeIntervalInMillis).isEqualTo(15000)
    }

    @Test
    fun `test renogy configuration initialization expect values from properties file`() {
        assertThat(renogyConfiguration.comPort).isEqualTo("/dev/ttyUSB0")
    }
}
