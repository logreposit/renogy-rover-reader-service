package com.logreposit.renogyrover.services.logreposit

import com.logreposit.renogyrover.communication.renogy.RenogyRamData

object SharedTestData {
    fun sampleRamData() = RenogyRamData(
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
