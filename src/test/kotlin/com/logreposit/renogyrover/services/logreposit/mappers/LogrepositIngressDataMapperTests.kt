package com.logreposit.renogyrover.services.logreposit.mappers

import com.logreposit.renogyrover.communication.renogy.RenogyRamData
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class LogrepositIngressDataMapperTests {
    @Test
    fun `test map to ingress dto without errors`() {
        val ramData = sampleRamData()

        val date = Instant.now()

        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = date,
                address = "5",
                data = ramData
        )

        assertThat(ingress.readings).hasSize(16)
        assertThat(ingress.readings.filter { it.measurement == "data" }).hasSize(1)
        assertThat(ingress.readings.filter { it.measurement == "faults" }).hasSize(15)

        val data = ingress.readings.first { it.measurement == "data" }

        assertThat(data.date).isEqualTo(date)
        assertThat(data.measurement).isEqualTo("data")
        assertThat(data.tags).hasSize(1)
        assertThat(data.tags[0].name).isEqualTo("device_address")
        assertThat(data.tags[0].value).isEqualTo("5")
        assertThat(data.fields).hasSize(33)
        assertThat(getIntegerField(data.fields, "battery_capacity_soc").value).isEqualTo(91)
        assertThat(getFloatField(data.fields, "battery_voltage").value).isEqualTo(25.1)
        assertThat(getFloatField(data.fields, "battery_charging_current").value).isEqualTo(8.72)
        assertThat(getIntegerField(data.fields, "controller_temperature").value).isEqualTo(32)
        assertThat(getIntegerField(data.fields, "battery_temperature").value).isEqualTo(23)
        assertThat(getFloatField(data.fields, "load_voltage").value).isEqualTo(25.2)
        assertThat(getFloatField(data.fields, "load_current").value).isEqualTo(2.15)
        assertThat(getIntegerField(data.fields, "load_power").value).isEqualTo(54)
        assertThat(getFloatField(data.fields, "solar_panel_voltage").value).isEqualTo(52.9)
        assertThat(getFloatField(data.fields, "solar_panel_current").value).isEqualTo(34.45)
        assertThat(getIntegerField(data.fields, "solar_panel_power").value).isEqualTo(1822)
        assertThat(getFloatField(data.fields, "daily_battery_voltage_min").value).isEqualTo(23.7)
        assertThat(getFloatField(data.fields, "daily_battery_voltage_max").value).isEqualTo(28.4)
        assertThat(getFloatField(data.fields, "daily_charging_current_max").value).isEqualTo(31.12)
        assertThat(getFloatField(data.fields, "daily_discharging_current_max").value).isEqualTo(2.91)
        assertThat(getIntegerField(data.fields, "daily_charging_power_max").value).isEqualTo(2410)
        assertThat(getIntegerField(data.fields, "daily_discharging_power_max").value).isEqualTo(78)
        assertThat(getIntegerField(data.fields, "daily_charging_amp_hrs").value).isEqualTo(87)
        assertThat(getIntegerField(data.fields, "daily_discharging_amp_hrs").value).isEqualTo(78)
        assertThat(getIntegerField(data.fields, "daily_power_generation").value).isEqualTo(2048)
        assertThat(getIntegerField(data.fields, "daily_power_consumption").value).isEqualTo(1024)
        assertThat(getIntegerField(data.fields, "total_operating_days").value).isEqualTo(9991)
        assertThat(getIntegerField(data.fields, "total_battery_over_discharges").value).isEqualTo(3)
        assertThat(getIntegerField(data.fields, "total_battery_full_charges").value).isEqualTo(500)
        assertThat(getIntegerField(data.fields, "total_battery_charging_amp_hrs").value).isEqualTo(4231)
        assertThat(getIntegerField(data.fields, "total_battery_discharging_amp_hrs").value).isEqualTo(1003)
        assertThat(getIntegerField(data.fields, "cumulative_power_generation").value).isEqualTo(82700)
        assertThat(getIntegerField(data.fields, "cumulative_power_consumption").value).isEqualTo(12601)
        assertThat(getIntegerField(data.fields, "load_status").value).isEqualTo(1)
        assertThat(getStringField(data.fields, "load_status_str").value).isEqualTo("ON")
        assertThat(getIntegerField(data.fields, "street_light_brightness").value).isEqualTo(55)
        assertThat(getIntegerField(data.fields, "charging_state").value).isEqualTo(2)
        assertThat(getStringField(data.fields, "charging_state_str").value).isEqualTo("MPPT")

        val faults = ingress.readings.filter { it.measurement == "faults" }

        assertThat(faults).allSatisfy { reading ->
            assertThat(reading.date).isEqualTo(date)
            assertThat(reading.measurement).isEqualTo("faults")
            assertThat(reading.tags).hasSize(2)
            assertThat(reading.tags.first { it.name == "device_address" }.value).isEqualTo("5")
            assertThat(reading.tags.filter { it.name == "fault_name"}).hasSize(1)
            assertThat(reading.fields).hasSize(2)
        }

        assertAllOk(readings = faults)
    }

    @Test
    fun `test map to ingress dto with charge_mos_short_circuit error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(chargeMosShortCircuit = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "charge_mos_short_circuit")
    }

    @Test
    fun `test map to ingress dto with anti_reverse_mos_short error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(antiReverseMosShort = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "anti_reverse_mos_short")
    }

    @Test
    fun `test map to ingress dto with solar_panel_reversely_connected error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(solarPanelReverselyConnected = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "solar_panel_reversely_connected")
    }

    @Test
    fun `test map to ingress dto with solar_panel_working_point_over_voltage error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(solarPanelWorkingPointOverVoltage = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "solar_panel_working_point_over_voltage")
    }

    @Test
    fun `test map to ingress dto with solar_panel_counter_current error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(solarPanelCounterCurrent = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "solar_panel_counter_current")
    }

    @Test
    fun `test map to ingress dto with photovoltaic_input_side_over_voltage error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(photovoltaicInputSideOverVoltage = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "photovoltaic_input_side_over_voltage")
    }

    @Test
    fun `test map to ingress dto with photovoltaic_input_side_short_circuit error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(photovoltaicInputSideShortCircuit = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "photovoltaic_input_side_short_circuit")
    }

    @Test
    fun `test map to ingress dto with photovoltaic_input_over_power error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(photovoltaicInputOverPower = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "photovoltaic_input_over_power")
    }

    @Test
    fun `test map to ingress dto with ambient_temperature_too_high error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(ambientTemperatureTooHigh = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "ambient_temperature_too_high")
    }

    @Test
    fun `test map to ingress dto with controller_temperature_too_high error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(controllerTemperatureTooHigh = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "controller_temperature_too_high")
    }

    @Test
    fun `test map to ingress dto with load_over_power_or_over_current error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(loadOverPowerOrOverCurrent = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "load_over_power_or_over_current")
    }

    @Test
    fun `test map to ingress dto with load_short_circuit error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(loadShortCircuit = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "load_short_circuit")
    }

    @Test
    fun `test map to ingress dto with battery_over_voltage error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(batteryOverVoltage = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "battery_over_voltage")
    }

    @Test
    fun `test map to ingress dto with battery_under_voltage error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(batteryUnderVoltage = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "battery_under_voltage")
    }

    @Test
    fun `test map to ingress dto with battery_over_discharge error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(batteryOverDischarge = true)
        )

        assertAllOkExcept(ingress = ingress, faultName = "battery_over_discharge")
    }

    @Test
    fun `test map to ingress dto with solar_panel_counter_current and battery_under_voltage error`() {
        val ingress = LogrepositIngressDataMapper.toLogrepositIngressDto(
                date = Instant.now(),
                address = "5",
                data = sampleRamData().copy(
                        solarPanelCounterCurrent = true,
                        batteryUnderVoltage = true
                )
        )

        val faults = ingress.readings.filter { it.measurement == "faults" }

        assertError(readings = faults, faultName = "solar_panel_counter_current")
        assertError(readings = faults, faultName = "battery_under_voltage")
    }

    private fun assertAllOk(readings: List<Reading>) =
        assertThat(readings).allSatisfy {
            assertThat(getIntegerField(fields = it.fields, name = "state").value).isEqualTo(0)
            assertThat(getStringField(fields = it.fields, name = "state_str").value).isEqualTo("OK")
        }

    private fun assertAllOkExcept(ingress: IngressData, faultName: String) {
        val faults = ingress.readings.filter { it.measurement == "faults" }
        val shouldBeOk = faults.filterNot { it.tags.contains(Tag(name = "fault_name", value = faultName)) }

        assertThat(shouldBeOk).hasSize(14)
        assertAllOk(shouldBeOk)
        assertError(readings = faults, faultName = faultName)
    }

    private fun assertError(readings: List<Reading>, faultName: String) {
        val reading = getFault(readings, faultName)

        assertThat(getIntegerField(fields = reading.fields, name = "state").value).isEqualTo(1)
        assertThat(getStringField(fields = reading.fields, name = "state_str").value).isEqualTo("ERROR")
    }

    private fun getFault(readings: List<Reading>, faultName: String) = readings.first {
        it.tags.contains(Tag(name = "fault_name", value = faultName))
    }

    private fun getIntegerField(fields: List<Field>, name: String) = fields.filter { name == it.name }.filterIsInstance(IntegerField::class.java).first()
    private fun getFloatField(fields: List<Field>, name: String) = fields.filter { name == it.name }.filterIsInstance(FloatField::class.java).first()
    private fun getStringField(fields: List<Field>, name: String) = fields.filter { name == it.name }.filterIsInstance(StringField::class.java).first()

    private fun sampleRamData() = RenogyRamData(
            batteryCapacitySoc = 91,
            batteryVoltage = 25.1,
            batteryChargingCurrent = 8.72,
            controllerTemperature = 32,
            batteryTemperature = 23,
            loadVoltage = 25.2,
            loadCurrent = 2.15,
            loadPower = 54,
            solarPanelVoltage = 52.9,
            solarPanelCurrent = 34.45,
            solarPanelPower = 1822,
            dailyBatteryVoltageMin = 23.7,
            dailyBatteryVoltageMax = 28.4,
            dailyChargingCurrentMax = 31.12,
            dailyDischargingCurrentMax = 2.91,
            dailyChargingPowerMax = 2410,
            dailyDischargingPowerMax = 78,
            dailyChargingAmpHrs = 87,
            dailyDischargingAmpHrs = 78,
            dailyPowerGeneration = 2048,
            dailyPowerConsumption = 1024,
            totalOperatingDays = 9991,
            totalBatteryOverDischarges = 3,
            totalBatteryFullCharges = 500,
            totalBatteryChargingAmpHrs = 4231,
            totalBatteryDischargingAmpHrs = 1003,
            cumulativePowerGeneration = 82700,
            cumulativePowerConsumption = 12601,
            loadStatus = true,
            streetLightBrightness = 55,
            chargingState = 2,
            chargeMosShortCircuit = false,
            antiReverseMosShort = false,
            solarPanelReverselyConnected = false,
            solarPanelWorkingPointOverVoltage = false,
            solarPanelCounterCurrent = false,
            photovoltaicInputSideOverVoltage = false,
            photovoltaicInputSideShortCircuit = false,
            photovoltaicInputOverPower = false,
            ambientTemperatureTooHigh = false,
            controllerTemperatureTooHigh = false,
            loadOverPowerOrOverCurrent = false,
            loadShortCircuit = false,
            batteryOverVoltage = false,
            batteryUnderVoltage = false,
            batteryOverDischarge = false
    )
}
