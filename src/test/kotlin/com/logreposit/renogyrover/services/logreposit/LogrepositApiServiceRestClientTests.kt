package com.logreposit.renogyrover.services.logreposit

import com.logreposit.renogyrover.configuration.LogrepositConfiguration
import com.logreposit.renogyrover.configuration.RetryConfiguration
import com.logreposit.renogyrover.services.logreposit.SharedTestData.sampleRamData
import org.assertj.core.api.Assertions
import org.hamcrest.Matchers.containsInAnyOrder
import org.hamcrest.Matchers.matchesPattern
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.client.ExpectedCount
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators
import org.springframework.web.client.HttpServerErrorException

@ExtendWith(SpringExtension::class)
@RestClientTest(LogrepositApiService::class)
@Import(RetryConfiguration::class)
class LogrepositApiServiceRestClientTests {
    @MockBean
    private lateinit var logrepositConfiguration: LogrepositConfiguration

    @Autowired
    private lateinit var client: LogrepositApiService

    @Autowired
    private lateinit var server: MockRestServiceServer

    @BeforeEach
    fun setUp() {
        given(logrepositConfiguration.apiBaseUrl).willReturn("https://api.logreposit.com")
        given(logrepositConfiguration.deviceToken).willReturn("TOKEN")
    }

    @Test
    fun `given valid data it should finish successfully`() {
        server.expect(ExpectedCount.once(), requestTo("https://api.logreposit.com/v2/ingress/data"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(jsonPath("$.readings").isArray)
            .andExpect(jsonPath("$.readings.length()").value(16))
            .andExpect(jsonPath("$.readings[0].date").isString)
            .andExpect(jsonPath("$.readings[0].date").value(matchesPattern("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2})\\:(\\d{2})\\:(\\d{2})(\\.\\d+)Z")))
            .andExpect(jsonPath("$.readings[0].tags").isArray)
            .andExpect(jsonPath("$.readings[0].tags.length()").value(1))
            .andExpect(jsonPath("$.readings[0].tags[0]").isMap)
            .andExpect(jsonPath("$.readings[0].tags[0].name").value("device_address"))
            .andExpect(jsonPath("$.readings[0].tags[0].value").value("1"))
            .andExpect(jsonPath("$.readings[0].fields").isArray)
            .andExpect(jsonPath("$.readings[0].fields.length()").value(33))
            .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"battery_voltage\")].value").value(25.1))
            .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"battery_voltage\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"solar_panel_power\")].value").value(1822))
            .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"solar_panel_power\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"charging_state_str\")].value").value("MPPT"))
            .andExpect(jsonPath("$.readings[0].fields[?(@.name == \"charging_state_str\")].datatype").value("STRING"))
            .andExpect(jsonPath("$.readings[9].tags.length()").value(2))
            .andExpect(jsonPath("$.readings[9].tags[0]").isMap)
            .andExpect(jsonPath("$.readings[9].tags[0].name").value("device_address"))
            .andExpect(jsonPath("$.readings[9].tags[0].value").value("1"))
            .andExpect(jsonPath("$.readings[9].tags[1]").isMap)
            .andExpect(jsonPath("$.readings[9].tags[1].name").value("fault_name"))
            .andExpect(jsonPath("$.readings[9].tags[1].value").value("ambient_temperature_too_high"))
            .andExpect(jsonPath("$.readings[9].fields").isArray)
            .andExpect(jsonPath("$.readings[9].fields.length()").value(2))
            .andExpect(jsonPath("$.readings[9].fields[?(@.name == \"state\")].value").value(0))
            .andExpect(jsonPath("$.readings[9].fields[?(@.name == \"state\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.readings[9].fields[?(@.name == \"state_str\")].value").value("OK"))
            .andExpect(jsonPath("$.readings[9].fields[?(@.name == \"state_str\")].datatype").value("STRING"))
            .andRespond(MockRestResponseCreators.withSuccess())

        client.pushData(renogyRamData = sampleRamData())

        server.verify()
    }

    @Test
    fun `given server error when pushing data it should retry it 4 times (5 times total) before giving up`() {
        server.expect(ExpectedCount.times(5), requestTo("https://api.logreposit.com/v2/ingress/data"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError())

        val started = System.currentTimeMillis()

        val thrown = assertThrows<HttpServerErrorException.InternalServerError> {
            client.pushData(renogyRamData = sampleRamData())
        }

        Assertions.assertThat(thrown.message).isEqualTo("500 Internal Server Error: [no body]")
        Assertions.assertThat(System.currentTimeMillis() - started).isBetween(2000, 3000)

        server.verify()
    }

    @Test
    fun `given client error entity unprocessable when pushing data it update the device ingress definition`() {
        server.expect(ExpectedCount.times(1), requestTo("https://api.logreposit.com/v2/ingress/data"))
            .andExpect(method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withStatus(HttpStatus.UNPROCESSABLE_ENTITY))

        server.expect(ExpectedCount.times(1), requestTo("https://api.logreposit.com/v2/ingress/definition"))
            .andExpect(method(HttpMethod.PUT))
            .andExpect(jsonPath("$.measurements").isArray)
            .andExpect(jsonPath("$.measurements.length()").value(2))
            .andExpect(jsonPath("$.measurements[0].name").value("data"))
            .andExpect(jsonPath("$.measurements[0].tags").isArray)
            .andExpect(jsonPath("$.measurements[0].tags.length()").value(1))
            .andExpect(jsonPath("$.measurements[0].tags[0]").value("device_address"))
            .andExpect(jsonPath("$.measurements[0].fields").isArray)
            .andExpect(jsonPath("$.measurements[0].fields.length()").value(33))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_capacity_soc\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_capacity_soc\")].description").value("Current battery capacity, state of charge (0-100) [%]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_voltage\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_voltage\")].description").value("Battery voltage [V]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_charging_current\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_charging_current\")].description").value("Charging current (to battery) [A]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"controller_temperature\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"controller_temperature\")].description").value("Temperature of the solar charge controller [Degrees Celsius]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_temperature\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"battery_temperature\")].description").value("Temperature of the battery / ambient temperature [Degrees Celsius]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_voltage\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_voltage\")].description").value("Load (street light) voltage [V]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_current\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_current\")].description").value("Load (street light) current [A]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_power\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_power\")].description").value("Load (street sight) power [W]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"solar_panel_voltage\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"solar_panel_voltage\")].description").value("Solar panel voltage [V]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"solar_panel_current\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"solar_panel_current\")].description").value("Solar panel charging current (to controller) [A]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"solar_panel_power\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"solar_panel_power\")].description").value("Solar panel charging power [W]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_battery_voltage_min\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_battery_voltage_min\")].description").value("Minimum battery voltage of the current day [V]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_battery_voltage_max\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_battery_voltage_max\")].description").value("Maximum battery voltage of the current day [V]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_charging_current_max\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_charging_current_max\")].description").value("Maximum charging current of the current day [A]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_discharging_current_max\")].datatype").value("FLOAT"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_discharging_current_max\")].description").value("Maximum discharging current of the current day [A]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_charging_power_max\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_charging_power_max\")].description").value("Maximum charging power of the current day [W]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_discharging_power_max\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_discharging_power_max\")].description").value("Maximum discharging power of the current day [W]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_charging_amp_hrs\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_charging_amp_hrs\")].description").value("Charging amp-hrs of the current day [Ah]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_discharging_amp_hrs\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_discharging_amp_hrs\")].description").value("Discharging amp-hrs of the current day [Ah]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_power_generation\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_power_generation\")].description").value("Power generation of the current day [Wh]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_power_consumption\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"daily_power_consumption\")].description").value("Power consumption of the current day [Wh]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_operating_days\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_operating_days\")].description").value("Total number of operating days"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_over_discharges\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_over_discharges\")].description").value("Total number of battery over-discharges"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_full_charges\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_full_charges\")].description").value("Total number of battery full-charges"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_charging_amp_hrs\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_charging_amp_hrs\")].description").value("Total charging amp-hrs of the battery [Ah]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_discharging_amp_hrs\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"total_battery_discharging_amp_hrs\")].description").value("Total discharging amp-hrs of the battery [Ah]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"cumulative_power_generation\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"cumulative_power_generation\")].description").value("Cumulative power generation [Wh]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"cumulative_power_consumption\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"cumulative_power_consumption\")].description").value("Cumulative power consumption [Wh]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_status\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_status\")].description").value("Load (street light) status (0/1)"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_status_str\")].datatype").value("STRING"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"load_status_str\")].description").value("Load (street light) status (ON/OFF)"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"street_light_brightness\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"street_light_brightness\")].description").value("Street light (load) brightness (0-100) [%]"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"charging_state\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"charging_state\")].description").value("Current charging state (0-6)"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"charging_state_str\")].datatype").value("STRING"))
            .andExpect(jsonPath("$.measurements[0].fields[?(@.name == \"charging_state_str\")].description").value("Current charging state (DEACTIVATED, ACTIVATED, MPPT, EQUALIZING, BOOST, FLOATING, CURRENT_LIMITING)"))
            .andExpect(jsonPath("$.measurements[1].name").value("faults"))
            .andExpect(jsonPath("$.measurements[1].tags").isArray)
            .andExpect(jsonPath("$.measurements[1].tags.length()").value(2))
            .andExpect(jsonPath("$.measurements[1].tags").value(containsInAnyOrder("device_address", "fault_name")))
            .andExpect(jsonPath("$.measurements[1].fields").isArray)
            .andExpect(jsonPath("$.measurements[1].fields.length()").value(2))
            .andExpect(jsonPath("$.measurements[1].fields[?(@.name == \"state\")].description").value("Indicates whether the fault is active or not (0/1)"))
            .andExpect(jsonPath("$.measurements[1].fields[?(@.name == \"state\")].datatype").value("INTEGER"))
            .andExpect(jsonPath("$.measurements[1].fields[?(@.name == \"state_str\")].description").value("Indicates whether the fault is active or not (OK/NOT_OK)"))
            .andExpect(jsonPath("$.measurements[1].fields[?(@.name == \"state_str\")].datatype").value("STRING"))
            .andRespond(MockRestResponseCreators.withSuccess())

        client.pushData(renogyRamData = sampleRamData())

        server.verify()
    }
}
