package com.logreposit.renogyroverapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "renogy")
class RenogyConfiguration {
    var comPort = "/dev/ttyUSB0"
}
