package com.logreposit.renogyrover.services.logreposit.mappers

import com.logreposit.renogyrover.communication.renogy.RenogyRamData
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.FloatField
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.IngressData
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.IntegerField
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.Reading
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.StringField
import com.logreposit.renogyrover.services.logreposit.dtos.ingress.Tag
import java.time.Instant

object LogrepositIngressDataMapper {
    fun toLogrepositIngressDto(date: Instant, address: String, data: RenogyRamData) = IngressData(
        readings = listOf(
            Reading(
                date = date,
                measurement = "data",
                tags = listOfNotNull(Tag(name = "device_address", value = address)),
                fields = listOfNotNull(
                    IntegerField(name = "battery_capacity_soc", value = data.batteryCapacitySoc.toLong()),
                    FloatField(name = "battery_voltage", value = data.batteryVoltage),
                    FloatField(name = "battery_charging_current", value = data.batteryChargingCurrent),
                    IntegerField(name = "controller_temperature", value = data.controllerTemperature.toLong()),
                    IntegerField(name = "battery_temperature", value = data.batteryTemperature.toLong()),
                    FloatField(name = "load_voltage", value = data.loadVoltage),
                    FloatField(name = "load_current", value = data.loadCurrent),
                    IntegerField(name = "load_power", value = data.loadPower.toLong()),
                    FloatField(name = "solar_panel_voltage", value = data.solarPanelVoltage),
                    FloatField(name = "solar_panel_current", value = data.solarPanelCurrent),
                    IntegerField(name = "solar_panel_power", value = data.solarPanelPower.toLong()),
                    FloatField(name = "daily_battery_voltage_min", value = data.dailyBatteryVoltageMin),
                    FloatField(name = "daily_battery_voltage_max", value = data.dailyBatteryVoltageMax),
                    FloatField(name = "daily_charging_current_max", value = data.dailyChargingCurrentMax),
                    FloatField(name = "daily_discharging_current_max", value = data.dailyDischargingCurrentMax),
                    IntegerField(name = "daily_charging_power_max", value = data.dailyChargingPowerMax.toLong()),
                    IntegerField(name = "daily_discharging_power_max", value = data.dailyDischargingPowerMax.toLong()),
                    IntegerField(name = "daily_charging_amp_hrs", value = data.dailyChargingAmpHrs.toLong()),
                    IntegerField(name = "daily_discharging_amp_hrs", value = data.dailyDischargingAmpHrs.toLong()),
                    IntegerField(name = "daily_power_generation", value = data.dailyPowerGeneration.toLong()),
                    IntegerField(name = "daily_power_consumption", value = data.dailyPowerConsumption.toLong()),
                    IntegerField(name = "total_operating_days", value = data.totalOperatingDays.toLong()),
                    IntegerField(name = "total_battery_over_discharges", value = data.totalBatteryOverDischarges.toLong()),
                    IntegerField(name = "total_battery_full_charges", value = data.totalBatteryFullCharges.toLong()),
                    IntegerField(name = "total_battery_charging_amp_hrs", value = data.totalBatteryChargingAmpHrs),
                    IntegerField(name = "total_battery_discharging_amp_hrs", value = data.totalBatteryDischargingAmpHrs),
                    IntegerField(name = "cumulative_power_generation", value = data.cumulativePowerGeneration),
                    IntegerField(name = "cumulative_power_consumption", value = data.cumulativePowerConsumption),
                    IntegerField(name = "load_status", value = boolToLong(data.loadStatus)),
                    StringField(name = "load_status_str", value = loadStatusToString(data.loadStatus)),
                    IntegerField(name = "street_light_brightness", value = data.streetLightBrightness.toLong()),
                    IntegerField(name = "charging_state", value = data.chargingState.toLong()),
                    StringField(name = "charging_state_str", value = chargingStateToString(data.chargingState))
                )
            ),
            createFault(date = date, address = address, faultName = "charge_mos_short_circuit", state = data.chargeMosShortCircuit),
            createFault(date = date, address = address, faultName = "anti_reverse_mos_short", state = data.antiReverseMosShort),
            createFault(date = date, address = address, faultName = "solar_panel_reversely_connected", state = data.solarPanelReverselyConnected),
            createFault(date = date, address = address, faultName = "solar_panel_working_point_over_voltage", state = data.solarPanelWorkingPointOverVoltage),
            createFault(date = date, address = address, faultName = "solar_panel_counter_current", state = data.solarPanelCounterCurrent),
            createFault(date = date, address = address, faultName = "photovoltaic_input_side_over_voltage", state = data.photovoltaicInputSideOverVoltage),
            createFault(date = date, address = address, faultName = "photovoltaic_input_side_short_circuit", state = data.photovoltaicInputSideShortCircuit),
            createFault(date = date, address = address, faultName = "photovoltaic_input_over_power", state = data.photovoltaicInputOverPower),
            createFault(date = date, address = address, faultName = "ambient_temperature_too_high", state = data.ambientTemperatureTooHigh),
            createFault(date = date, address = address, faultName = "controller_temperature_too_high", state = data.controllerTemperatureTooHigh),
            createFault(date = date, address = address, faultName = "load_over_power_or_over_current", state = data.loadOverPowerOrOverCurrent),
            createFault(date = date, address = address, faultName = "load_short_circuit", state = data.loadShortCircuit),
            createFault(date = date, address = address, faultName = "battery_over_voltage", state = data.batteryOverVoltage),
            createFault(date = date, address = address, faultName = "battery_under_voltage", state = data.batteryUnderVoltage),
            createFault(date = date, address = address, faultName = "battery_over_discharge", state = data.batteryOverDischarge)
        )
    )

    private fun boolToLong(bool: Boolean) = when (bool) {
        true -> 1L
        false -> 0L
    }

    private fun loadStatusToString(status: Boolean) = when (status) {
        true -> "ON"
        false -> "OFF"
    }

    private fun chargingStateToString(status: Int) = when (status) {
        0 -> "DEACTIVATED"
        1 -> "ACTIVATED"
        2 -> "MPPT"
        3 -> "EQUALIZING"
        4 -> "BOOST"
        5 -> "FLOATING"
        6 -> "CURRENT_LIMITING"
        else -> "UNKNOWN"
    }

    private fun createFault(date: Instant, address: String, faultName: String, state: Boolean) = Reading(
        date = date,
        measurement = "faults",
        tags = listOfNotNull(
            Tag(name = "device_address", value = address),
            Tag(name = "fault_name", value = faultName)
        ),
        fields = listOfNotNull(
            IntegerField(name = "state", value = boolToLong(state)),
            StringField(name = "state_str", value = stateToString(state = state))
        )
    )

    private fun stateToString(state: Boolean) = when (state) {
        true -> "ERROR"
        false -> "OK"
    }
}
