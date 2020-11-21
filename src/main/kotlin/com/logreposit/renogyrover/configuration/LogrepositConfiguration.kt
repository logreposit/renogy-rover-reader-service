package com.logreposit.renogyrover.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "logreposit")
class LogrepositConfiguration {
    var apiBaseUrl: String? = "https://api.logreposit.com"
    var deviceToken: String? = "INVALID"
    var scrapeIntervalInMillis: Long? = 15000
}
