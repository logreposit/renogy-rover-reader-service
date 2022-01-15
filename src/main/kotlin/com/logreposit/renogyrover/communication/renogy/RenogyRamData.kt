package com.logreposit.renogyrover.communication.renogy

data class RenogyRamData(
    val batteryCapacitySoc: Int,
    val batteryVoltage: Double,
    val batteryChargingCurrent: Double,
    val controllerTemperature: Int,
    val batteryTemperature: Int,
    val loadVoltage: Double,
    val loadCurrent: Double,
    val loadPower: Int,
    val solarPanelVoltage: Double,
    val solarPanelCurrent: Double,
    val solarPanelPower: Int,
    val dailyBatteryVoltageMin: Double,
    val dailyBatteryVoltageMax: Double,
    val dailyChargingCurrentMax: Double,
    val dailyDischargingCurrentMax: Double,
    val dailyChargingPowerMax: Int,
    val dailyDischargingPowerMax: Int,
    val dailyChargingAmpHrs: Int,
    val dailyDischargingAmpHrs: Int,
    val dailyPowerGeneration: Int,
    val dailyPowerConsumption: Int,
    val totalOperatingDays: Int,
    val totalBatteryOverDischarges: Int,
    val totalBatteryFullCharges: Int,
    val totalBatteryChargingAmpHrs: Long,
    val totalBatteryDischargingAmpHrs: Long,
    val cumulativePowerGeneration: Long,
    val cumulativePowerConsumption: Long,
    val loadStatus: Boolean,
    val streetLightBrightness: Int,
    val chargingState: Int,
    val chargeMosShortCircuit: Boolean,
    val antiReverseMosShort: Boolean,
    val solarPanelReverselyConnected: Boolean,
    val solarPanelWorkingPointOverVoltage: Boolean,
    val solarPanelCounterCurrent: Boolean,
    val photovoltaicInputSideOverVoltage: Boolean,
    val photovoltaicInputSideShortCircuit: Boolean,
    val photovoltaicInputOverPower: Boolean,
    val ambientTemperatureTooHigh: Boolean,
    val controllerTemperatureTooHigh: Boolean,
    val loadOverPowerOrOverCurrent: Boolean,
    val loadShortCircuit: Boolean,
    val batteryOverVoltage: Boolean,
    val batteryUnderVoltage: Boolean,
    val batteryOverDischarge: Boolean
)
