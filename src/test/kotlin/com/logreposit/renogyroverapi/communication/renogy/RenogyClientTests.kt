package com.logreposit.renogyroverapi.communication.renogy

import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import jdk.nashorn.internal.ir.annotations.Ignore
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RenogyClientTests {

    private val renogySerialClient: RenogySerialClient = mock()

    @Ignore // TODO: test!
    @Test
    fun `test get ram data expect parsing successful`() {
        whenever(renogySerialClient.readRamRegisters()).thenReturn(getSampleRegisters())

        val data = RenogyClient(renogySerialClient).getRamData()

        assertThat(data.batteryCapacitySoc).isEqualTo(100)
    }

    // test: different battery voltage, ...
    // test: under voltage error (set bit x in byte y)
    // test: temperature
    // test: minus temperature
    // test: ..

    private fun getSampleRegisters() = arrayOf(
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0),
            SimpleInputRegister(0)
    )
}
