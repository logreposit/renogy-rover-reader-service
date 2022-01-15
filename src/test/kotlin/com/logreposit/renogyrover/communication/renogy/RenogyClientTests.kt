package com.logreposit.renogyrover.communication.renogy

import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import kotlin.experimental.and

class RenogyClientTests {

    private val renogySerialClient: RenogySerialClient = mock()

    @Test
    fun `test get ram data expect parsing successful without reported errors`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(getSampleRegisters())

        val data = RenogyClient(renogySerialClient).getRamData()

        assertThat(data.batteryCapacitySoc).isEqualTo(100)
        assertThat(data.batteryVoltage).isEqualTo(25.8)
        assertThat(data.batteryChargingCurrent).isEqualTo(15.82)
        assertThat(data.controllerTemperature).isEqualTo(19)
        assertThat(data.batteryTemperature).isEqualTo(-7)
        assertThat(data.loadVoltage).isEqualTo(25.7)
        assertThat(data.loadCurrent).isEqualTo(5.59)
        assertThat(data.loadPower).isEqualTo(144)
        assertThat(data.solarPanelVoltage).isEqualTo(31.2)
        assertThat(data.solarPanelCurrent).isEqualTo(16.82)
        assertThat(data.solarPanelPower).isEqualTo(525)
        assertThat(data.dailyBatteryVoltageMin).isEqualTo(23.8)
        assertThat(data.dailyBatteryVoltageMax).isEqualTo(27.9)
        assertThat(data.dailyChargingCurrentMax).isEqualTo(19.99)
        assertThat(data.dailyDischargingCurrentMax).isEqualTo(2.56)
        assertThat(data.dailyChargingPowerMax).isEqualTo(558)
        assertThat(data.dailyDischargingPowerMax).isEqualTo(61)
        assertThat(data.dailyChargingAmpHrs).isEqualTo(101)
        assertThat(data.dailyDischargingAmpHrs).isEqualTo(32)
        assertThat(data.dailyPowerGeneration).isEqualTo(4200)
        assertThat(data.dailyPowerConsumption).isEqualTo(441)
        assertThat(data.totalOperatingDays).isEqualTo(512)
        assertThat(data.totalBatteryOverDischarges).isEqualTo(9)
        assertThat(data.totalBatteryFullCharges).isEqualTo(128)
        assertThat(data.totalBatteryChargingAmpHrs).isEqualTo(548674560)
        assertThat(data.totalBatteryDischargingAmpHrs).isEqualTo(448674560)
        assertThat(data.cumulativePowerGeneration).isEqualTo(987654321)
        assertThat(data.cumulativePowerConsumption).isEqualTo(456789321)
        assertThat(data.loadStatus).isTrue()
        assertThat(data.streetLightBrightness).isEqualTo(33)
        assertThat(data.chargingState).isEqualTo(2)
        assertThat(data.chargeMosShortCircuit).isFalse()
        assertThat(data.antiReverseMosShort).isFalse()
        assertThat(data.solarPanelReverselyConnected).isFalse()
        assertThat(data.solarPanelWorkingPointOverVoltage).isFalse()
        assertThat(data.solarPanelCounterCurrent).isFalse()
        assertThat(data.photovoltaicInputSideOverVoltage).isFalse()
        assertThat(data.photovoltaicInputSideShortCircuit).isFalse()
        assertThat(data.photovoltaicInputOverPower).isFalse()
        assertThat(data.ambientTemperatureTooHigh).isFalse()
        assertThat(data.controllerTemperatureTooHigh).isFalse()
        assertThat(data.loadOverPowerOrOverCurrent).isFalse()
        assertThat(data.loadShortCircuit).isFalse()
        assertThat(data.batteryUnderVoltage).isFalse()
        assertThat(data.batteryOverVoltage).isFalse()
        assertThat(data.batteryOverDischarge).isFalse()
    }

    @Test
    fun `test get ram data expect parsing successful with load state off`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(clearBit(registerBytes[0], 7), registerBytes[1])
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().loadStatus).isFalse()
    }

    @Test
    fun `test get ram data expect parsing successful with load state off and brightness 99`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(toUByte(0x63), registerBytes[1])
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().streetLightBrightness).isEqualTo(99)
    }

    @Test
    fun `test get ram data expect parsing successful with charging state disabled`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(registerBytes[0], toUByte(0x00))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().chargingState).isEqualTo(0)
    }

    @Test
    fun `test get ram data expect parsing successful with charging state activated`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(registerBytes[0], toUByte(0x01))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().chargingState).isEqualTo(1)
    }

    @Test
    fun `test get ram data expect parsing successful with charging state mppt`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(registerBytes[0], toUByte(0x02))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().chargingState).isEqualTo(2)
    }

    @Test
    fun `test get ram data expect parsing successful with charging state equalizing`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(registerBytes[0], toUByte(0x03))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().chargingState).isEqualTo(3)
    }

    @Test
    fun `test get ram data expect parsing successful with charging state boost`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(registerBytes[0], toUByte(0x04))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().chargingState).isEqualTo(4)
    }

    @Test
    fun `test get ram data expect parsing successful with charging state floating`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(registerBytes[0], toUByte(0x05))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().chargingState).isEqualTo(5)
    }

    @Test
    fun `test get ram data expect parsing successful with charging state current limiting`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                val registerBytes = it[32].toBytes()
                it[32] = SimpleInputRegister(registerBytes[0], toUByte(0x06))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().chargingState).isEqualTo(6)
    }

    @Test
    fun `test get ram data expect parsing successful with ambient temperature too high error`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                it[34] = SimpleInputRegister(toUByte(0x00), toUByte(0x40))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().ambientTemperatureTooHigh).isTrue()
    }

    @Test
    fun `test get ram data expect parsing successful with solar panel counter current error`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(
            getSampleRegisters().also {
                it[34] = SimpleInputRegister(toUByte(0x04), toUByte(0x00))
            }
        )

        assertThat(RenogyClient(renogySerialClient).getRamData().solarPanelCounterCurrent).isTrue()
    }

    private fun getSampleRegisters() = arrayOf(
        SimpleInputRegister(toUByte(0x00), toUByte(0x64)),
        SimpleInputRegister(toUByte(0x01), toUByte(0x02)),
        SimpleInputRegister(toUByte(0x06), toUByte(0x2E)),
        SimpleInputRegister(toUByte(0x13), toUByte(0x87)),
        SimpleInputRegister(toUByte(0x01), toUByte(0x01)),
        SimpleInputRegister(toUByte(0x02), toUByte(0x2F)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x90)),
        SimpleInputRegister(toUByte(0x01), toUByte(0x38)),
        SimpleInputRegister(toUByte(0x06), toUByte(0x92)),
        SimpleInputRegister(toUByte(0x02), toUByte(0x0D)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x00)),
        SimpleInputRegister(toUByte(0x00), toUByte(0xEE)),
        SimpleInputRegister(toUByte(0x01), toUByte(0x17)),
        SimpleInputRegister(toUByte(0x07), toUByte(0xCF)),
        SimpleInputRegister(toUByte(0x01), toUByte(0x00)),
        SimpleInputRegister(toUByte(0x02), toUByte(0x2E)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x3D)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x65)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x20)),
        SimpleInputRegister(toUByte(0x10), toUByte(0x68)),
        SimpleInputRegister(toUByte(0x01), toUByte(0xB9)),
        SimpleInputRegister(toUByte(0x02), toUByte(0x00)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x09)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x80)),
        SimpleInputRegister(toUByte(0x20), toUByte(0xB4)),
        SimpleInputRegister(toUByte(0x1C), toUByte(0x00)),
        SimpleInputRegister(toUByte(0x1A), toUByte(0xBE)),
        SimpleInputRegister(toUByte(0x3B), toUByte(0x00)),
        SimpleInputRegister(toUByte(0x3A), toUByte(0xDE)),
        SimpleInputRegister(toUByte(0x68), toUByte(0xB1)),
        SimpleInputRegister(toUByte(0x1B), toUByte(0x3A)),
        SimpleInputRegister(toUByte(0x0D), toUByte(0x49)),
        SimpleInputRegister(toUByte(0xA1), toUByte(0x02)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x00)),
        SimpleInputRegister(toUByte(0x00), toUByte(0x00))
    )

    private fun toUByte(i: Int): Byte = (i and 0xFFFF).toByte()

    private fun clearBit(value: Byte, position: Int): Byte = (value and (1 shl position).inv().toByte())
}
