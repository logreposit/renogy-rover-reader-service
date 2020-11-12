package com.logreposit.renogyroverapi.communication.renogy

import com.ghgande.j2mod.modbus.procimg.Register
import com.logreposit.renogyroverapi.utils.logger
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.nio.ByteBuffer
import kotlin.experimental.and

@Service
class RenogyClient(private val renogySerialClient: RenogySerialClient) {
    val logger = logger()

    fun getRamData(): RenogyRamData = parseRamData(renogySerialClient.readRamRegisters())

    // chargingStatus
    //     0 - charging deactivated
    //     1 - charging activated
    //     2 - mppt charging mode
    //     3 - equalizing charging mode
    //     4 - boost charging mode
    //     5 - floating charging mode
    //     6 - current limiting (overpower)

    private fun parseRamData(registers: Array<out Register>): RenogyRamData {
        val temperatures = registers[3].toBytes()
        val loadAndChargingStatus = registers[32].toBytes()
        val faultsAndWarnings = registers[34].toBytes()

        return RenogyRamData(
                batteryCapacitySoc = registers[0].toUnsignedShort(),
                batteryVoltage = parseVolts(registers[1]),
                batteryChargingCurrent = parseAmperes(registers[2]),
                controllerTemperature = parseTemperature(temperatures[0]),
                batteryTemperature = parseTemperature(temperatures[1]),
                loadVoltage = parseVolts(registers[4]),
                loadCurrent = parseAmperes(registers[5]),
                loadPower = registers[6].toUnsignedShort(),
                solarPanelVoltage = parseVolts(registers[7]),
                solarPanelCurrent = parseAmperes(registers[8]),
                solarPanelPower = registers[9].toUnsignedShort(),
                dailyBatteryVoltageMin = parseVolts(registers[11]),
                dailyBatteryVoltageMax = parseVolts(registers[12]),
                dailyChargingCurrentMax = parseAmperes(registers[13]),
                dailyDischargingCurrentMax = parseAmperes(registers[14]),
                dailyChargingPowerMax = registers[15].toUnsignedShort(),
                dailyDischargingPowerMax = registers[16].toUnsignedShort(),
                dailyChargingAmpHrs = registers[17].toUnsignedShort(),
                dailyDischargingAmpHrs = registers[18].toUnsignedShort(),
                dailyPowerGeneration = registers[19].toUnsignedShort(),
                dailyPowerConsumption = registers[20].toUnsignedShort(),
                totalOperatingDays = registers[21].toUnsignedShort(),
                totalBatteryOverDischarges = registers[22].toUnsignedShort(),
                totalBatteryFullCharges = registers[23].toUnsignedShort(),
                totalBatteryChargingAmpHrs = twoRegistersToUnsignedInt(registers[24], registers[25]),
                totalBatteryDischargingAmpHrs = twoRegistersToUnsignedInt(registers[26], registers[27]),
                cumulativePowerGeneration = twoRegistersToUnsignedInt(registers[28], registers[29]),
                cumulativePowerConsumption = twoRegistersToUnsignedInt(registers[30], registers[31]),
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
        ).also {
            logger.debug("Got data from solar charge controller: {}", it)
        }
    }

    private fun parseVolts(register: Register) = BigDecimal(register.value).multiply(BigDecimal("0.1")).toDouble()

    private fun parseAmperes(register: Register) = BigDecimal(register.value).multiply(BigDecimal("0.01")).toDouble()

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

    private fun twoRegistersToUnsignedInt(mostSignificantRegister: Register, leastSignificantRegister: Register): Long {

        fun toUnsignedInt(bytes: ByteArray): Long {
            val buffer = ByteBuffer.allocate(8).put(byteArrayOf(0, 0, 0, 0)).put(bytes)
            buffer.position(0)
            return buffer.long
        }

        val mostSignificantByteArray = mostSignificantRegister.toBytes()
        val leastSignificantByteArray = leastSignificantRegister.toBytes()

        val combinedByteArray = byteArrayOf(
                mostSignificantByteArray[0],
                mostSignificantByteArray[1],
                leastSignificantByteArray[0],
                leastSignificantByteArray[1]
        )

        return toUnsignedInt(combinedByteArray)
    }
}
