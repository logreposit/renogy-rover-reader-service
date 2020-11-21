package com.logreposit.renogyrover.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "renogy")
class RenogyConfiguration {
    var comPort: String? = null
}
