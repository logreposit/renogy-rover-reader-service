package com.logreposit.renogyroverapi.communication.serial

import com.logreposit.renogyroverapi.communication.renogy.RenogyClient
import com.logreposit.renogyroverapi.communication.renogy.RenogySerialClient
import com.logreposit.renogyroverapi.configuration.RenogyConfiguration
import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.jupiter.api.Test

class RenogyClientTests {

    @Ignore
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
    }
}
