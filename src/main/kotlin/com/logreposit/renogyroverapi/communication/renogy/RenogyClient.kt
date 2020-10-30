package com.logreposit.renogyroverapi.communication.renogy

import com.ghgande.j2mod.modbus.procimg.Register
import com.logreposit.renogyroverapi.utils.logger
import org.springframework.stereotype.Service
import kotlin.experimental.and

@Service
class RenogyClient(private val renogySerialClient: RenogySerialClient) {
    val logger = logger()

    fun getRamData(): RenogyRamData = parseRamData(renogySerialClient.readRamRegisters())

    private fun parseRamData(registers: Array<out Register>): RenogyRamData {
        val temperatures = registers[3].toBytes()
        val loadAndChargingStatus = registers[32].toBytes()
        val faultsAndWarnings = registers[34].toBytes()

        return RenogyRamData(
                batteryCapacitySoc = registers[0].value,
                batteryVoltage = parseVolts(registers[1]),
                batteryChargingCurrent = parseAmperes(registers[2]),
                controllerTemperature = parseTemperature(temperatures[0]),
                batteryTemperature = parseTemperature(temperatures[1]),
                loadVoltage = parseVolts(registers[4]),
                loadCurrent = parseAmperes(registers[5]),
                loadPower = registers[6].value,
                solarPanelVoltage = parseVolts(registers[7]),
                solarPanelCurrent = parseAmperes(registers[8]),
                solarPanelPower = registers[9].value,
                dailyBatteryVoltageMin = parseVolts(registers[11]),
                dailyBatteryVoltageMax = parseVolts(registers[12]),
                dailyChargingCurrentMax = parseAmperes(registers[13]),
                dailyDischargingCurrentMax = parseAmperes(registers[14]),
                dailyChargingPowerMax = registers[15].value,
                dailyDischargingPowerMax = registers[16].value,
                dailyChargingAmpHrs = registers[17].value,
                dailyDischargingAmpHrs = registers[18].value,
                dailyPowerGeneration = registers[19].value,
                dailyPowerConsumption = registers[20].value,
                totalOperatingDays = registers[21].value,
                totalBatteryOverDischarges = registers[22].value,
                totalBatteryFullCharges = registers[23].value,
                totalBatteryChargingAmpHrs = twoRegistersToInt(registers[24], registers[25]),
                totalBatteryDischargingAmpHrs = twoRegistersToInt(registers[26], registers[27]),
                cumulativePowerGeneration = twoRegistersToInt(registers[28], registers[29]),
                cumulativePowerConsumption = twoRegistersToInt(registers[30], registers[31]),
                loadStatus = isBitSetInByte(loadAndChargingStatus[0], 7),
                streetLightBrightness = clearBit(loadAndChargingStatus[0], 7).toInt(),
                chargingState = loadAndChargingStatus[1].toInt(),
                chargeMosShortCircuit = isBitSetInByte(faultsAndWarnings[0], 6),
                antiReverseMosShort = isBitSetInByte(faultsAndWarnings[0], 5),
                solarPanelReverselyConnected = isBitSetInByte(faultsAndWarnings[0], 4),
                solarPanelWorkingPointOverVoltage = isBitSetInByte(faultsAndWarnings[0], 3),
                solarPanelCounterCurrent = isBitSetInByte(faultsAndWarnings[0], 2),
                photovoltaicInputSideOverVoltage = isBitSetInByte(faultsAndWarnings[0], 1),
                photovoltaicInputSideShortCircuit = isBitSetInByte(faultsAndWarnings[0], 0),
                photovoltaicInputOverPower = isBitSetInByte(faultsAndWarnings[1], 7),
                ambientTemperatureTooHigh = isBitSetInByte(faultsAndWarnings[1], 6),
                controllerTemperatureTooHigh = isBitSetInByte(faultsAndWarnings[1], 5),
                loadOverPowerOrOverCurrent = isBitSetInByte(faultsAndWarnings[1], 4),
                loadShortCircuit = isBitSetInByte(faultsAndWarnings[1], 3),
                batteryUnderVoltage = isBitSetInByte(faultsAndWarnings[1], 2),
                batteryOverVoltage = isBitSetInByte(faultsAndWarnings[1], 1),
                batteryOverDischarge = isBitSetInByte(faultsAndWarnings[1], 0)
        )
    }

    private fun parseVolts(register: Register): Double = register.value * 0.1

    private fun parseAmperes(register: Register): Double = register.value * 0.01

    private fun parseTemperature(temperatureWithSigning: Byte): Int {
        val isNegative = isBitSetInByte(temperatureWithSigning, 7)
        val temperatureWithoutSigning = clearBit(temperatureWithSigning, 7)

        return when (isNegative) {
            true -> temperatureWithoutSigning.toInt() * -1
            false -> temperatureWithoutSigning.toInt()
        }
    }

    private fun isBitSetInByte(byte: Byte, position: Int): Boolean =
            when((byte.toInt() shr position) and 1) {
                1 -> true
                else -> false
            }

    private fun clearBit(value: Byte, position: Int): Byte = (value and (1 shl position).inv().toByte())

    private fun twoRegistersToInt(mostSignificantRegister: Register, leastSignificantRegister: Register): Int {
        val mostSignificantByteArray = mostSignificantRegister.toBytes()
        val leastSignificantByteArray = leastSignificantRegister.toBytes()

        val combinedByteArray = byteArrayOf(
                leastSignificantByteArray[1],
                leastSignificantByteArray[0],
                mostSignificantByteArray[1],
                mostSignificantByteArray[0]
        )

        return littleEndianConversion(combinedByteArray)
    }

    private fun littleEndianConversion(bytes: ByteArray): Int {
        var result = 0

        for (i in bytes.indices) {
            result = result or (bytes[i].toInt() shl 8 * i)
        }

        return result
    }
}
