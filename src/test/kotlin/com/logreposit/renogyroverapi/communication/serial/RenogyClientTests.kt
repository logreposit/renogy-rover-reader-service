package com.logreposit.renogyroverapi.communication.serial

import com.ghgande.j2mod.modbus.procimg.Register
import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister
import com.logreposit.renogyroverapi.communication.renogy.RenogyClient
import com.logreposit.renogyroverapi.communication.renogy.RenogySerialClient
import com.logreposit.renogyroverapi.configuration.RenogyConfiguration
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.experimental.and

class RenogyClientTests {

    @Test
    fun `test renogy`() {
        val client = RenogyClient(RenogySerialClient(RenogyConfiguration().also { it.comPort = "/dev/tty.usbserial-AC00JCNA" }))

        while (true) {
            val ramData = client.getRamData()

            //println("Battery SoC: ${ramData.batteryCapacitySoc}, Battery Voltage: ${ramData.batteryVoltage}, Battery OverV: ${ramData.batteryOverVoltage}, Battery UnderV: ${ramData.batteryUnderVoltage}, Battery OverD: ${ramData.batteryOverDischarge}")
            println("Daily Power Consumption: ${ramData.dailyPowerConsumption} \nCumul Power Cons: ${ramData.cumulativePowerConsumption} \nTotal disch amp hrs: ${ramData.totalBatteryDischargingAmpHrs}")
            println("\n")

            Thread.sleep(1000)
        }

        val asdf = "asdf"
    }

    @Test
    fun `test asdf`() {
        val client = RenogySerialClient(RenogyConfiguration().also { it.comPort = "/dev/tty.usbserial-AC00JCNA" })

        // Sent: 01 03 01 00 00 23 05 EF

        val result = client.readRamRegisters();

        val batteryCapacitySoc = result[0].value // % [0-100]
        val batteryVoltage = result[1].value * 0.1 // Volt
        val chargingCurrent = result[2].value * 0.01 // Ampere

        val temperatures = result[3].toBytes()

        val controllerTemperature = getTemperature(temperatures[0]) // Degrees Celsius
        val batteryTemperature = getTemperature(temperatures[1]) // Degrees Celsius

        val loadVoltage = result[4].value * 0.1 // Volt
        val loadCurrent = result[5].value * 0.01 // Ampere
        val loadPower = result[6].value // Watt

        val solarPanelVoltage = result[7].value * 0.1 // Volt
        val solarPanelCurrent = result[8].value * 0.01 // Ampere
        val chargingPower = result[9].value // Watt

        val loadStatus = result[10].value // according to doc write only? Boolean: 0 / 1

        val dailyBatteryVoltageMin = result[11].value * 0.1 // Volt
        val dailyBatteryVoltageMax = result[12].value * 0.1 // Volt
        val dailyChargingCurrentMax = result[13].value * 0.01 // Ampere
        val dailyDischargingCurrentMax = result[14].value * 0.01 // Ampere
        val dailyChargingPowerMax = result[15].value // Watt
        val dailyDischargingPowerMax = result[16].value // Watt
        val dailyChargingAmpereHours = result[17].value // Ah
        val dailyDischargingAmpereHours = result[18].value // Ah
        val dailyPowerGeneration = result[19].value // ??
        val dailyPowerConsumption = result[20].value // ??

        val totalOperatingDays = result[21].value
        val totalNumberOfBatteryOverDischarges = result[22].value
        val totalNumberOfBatteryFullCharges = result[23].value

        val totalChargingAmpereHours = twoRegistersToInteger(result[24], result[25]) // Ah
        val totalDischargingAmpereHours = twoRegistersToInteger(result[26], result[27]) // Ah
        val cumulativePowerGeneration = twoRegistersToInteger(result[28], result[29]) // ??
        val cumulativePowerConsumption = twoRegistersToInteger(result[30], result[31]) // ??

        val loadAndChargingStatus = result[32].toBytes()

        val streetLightStatus = loadAndChargingStatus[0]
        val streetLightIsOn = isBitSetInByte(streetLightStatus, 7) // / Boolean 0/1
        val streetLightBrightness = clearBit(streetLightStatus, 7).toInt() // 0 to 100 percent

        val chargingStatus = loadAndChargingStatus[1]
        // chargingStatus
        //     0 - charging deactivated
        //     1 - charging activated
        //     2 - mppt charging mode
        //     3 - equalizing charging mode
        //     4 - boost charging mode
        //     5 - floating charging mode
        //     6 - current limiting (overpower)

        val controllerErrorsAndWarnings1 = result[33] // reserved - is fia nix
        val faultAndWarnings = result[34]
        val faultAndWarningBytes = faultAndWarnings.toBytes()

        val isBatteryOverDischarge = isBitSetInByte(faultAndWarningBytes[1], 0)       // B16
        val isBatteryOverVoltage = isBitSetInByte(faultAndWarningBytes[1], 1)         // B17
        val isBatteryUnderVoltage = isBitSetInByte(faultAndWarningBytes[1], 2)        // B18
        val isLoadShortCircuit = isBitSetInByte(faultAndWarningBytes[1], 3)           // B19
        val isLoadOverpowerOrOverCurrent = isBitSetInByte(faultAndWarningBytes[1], 4) // B20
        val isControllerTempTooHigh = isBitSetInByte(faultAndWarningBytes[1], 5)      // B21
        val isAmbientTempTooHigh = isBitSetInByte(faultAndWarningBytes[1], 6)         // B22
        val pvInputOverPower = isBitSetInByte(faultAndWarningBytes[1], 7)             // B23
        val pvInputShortCirciut = isBitSetInByte(faultAndWarningBytes[0], 0)          // B24
        val pvInputOvervoltage = isBitSetInByte(faultAndWarningBytes[0], 1)           // B25
        val solarPanelCounterCurrent = isBitSetInByte(faultAndWarningBytes[0], 2)     // B26
        val solarPanelWorkingPointOvervoltage = isBitSetInByte(faultAndWarningBytes[0], 3) // B27
        val solarPanelReverselyConnected = isBitSetInByte(faultAndWarningBytes[0], 4) // B28
        val antiReverseMosShort = isBitSetInByte(faultAndWarningBytes[0], 5) // B29
        val chargeMosShortCircuit = isBitSetInByte(faultAndWarningBytes[0], 6) // B30
        // val reserved = isBitSetInByte(faultAndWarningBytes[0], 7) // B31


        val asdf = "asdf"
    }

    // TODO: Put in test
    private fun testTwoRegisters() {
        val r24 = SimpleInputRegister(3, 2)
        val r25 = SimpleInputRegister(6, 5)

        val totalChargingAmpereHours = twoRegistersToInteger(r24, r25)

        val str = totalChargingAmpereHours.toString(radix = 2)

        val str2 = ""
    }

    private fun getTemperature(temperatureWithSigning: Byte): Int {
        val isNegative = isBitSetInByte(temperatureWithSigning, 7)
        val temperatureWithoutSigning = clearBit(temperatureWithSigning, 7)

        return when (isNegative) {
            true -> temperatureWithoutSigning.toInt() * -1
            false -> temperatureWithoutSigning.toInt()
        }
    }

    private fun clearBit(value: Byte, position: Int): Byte {
        return (value and (1 shl position).inv().toByte())
    }

    private fun twoRegistersToInteger(mostSignificantRegister: Register, leastSignificantRegister: Register): Int {
        val mostSignificantByteArray = mostSignificantRegister.toBytes()   // 24
        val leastSignificantByteArray = leastSignificantRegister.toBytes() // 25

        //val combinedByteArray = byteArrayOf(mostSignificantByteArray[1], mostSignificantByteArray[0], leastSignificantByteArray[1], leastSignificantByteArray[0])
        val combinedByteArray = byteArrayOf(leastSignificantByteArray[1], leastSignificantByteArray[0], mostSignificantByteArray[1], mostSignificantByteArray[0])
        val integer = littleEndianConversion(combinedByteArray)

        return integer
    }

    private fun littleEndianConversion(bytes: ByteArray): Int {
        var result = 0
        for (i in bytes.indices) {
            result = result or (bytes[i].toInt() shl 8 * i)
        }
        return result
    }

    private fun isBitSetInByte(byte: Byte, position: Int): Boolean =
            when((byte.toInt() shr position) and 1) {
                1 -> true
                else -> false
            }
}
