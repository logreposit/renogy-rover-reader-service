package com.logreposit.renogyrover.communication.renogy

import com.fazecast.jSerialComm.SerialPortInvalidPortException
import com.logreposit.renogyrover.configuration.RenogyConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class RenogySerialClientTests {
    @Test
    fun `test connect to slave without specifying a valid rs232 serial adapter connected to a solar charge controller`() {
        val serialClient = RenogySerialClient(RenogyConfiguration().also {
            it.comPort = "/dev/ttyUSB0"
        })

        val e = assertThrows<RenogySerialClientException> {
            serialClient.readRamRegisters()
        }

        assertThat(e).hasMessage("Caught Exception while establishing connection to slave using Modbus RTU");
        assertThat(e).hasCauseExactlyInstanceOf(SerialPortInvalidPortException::class.java)
        assertThat(e.cause!!).hasMessage("Unable to create a serial port object from the invalid port descriptor: /dev/ttyUSB0")
        assertThat(e.cause!!.cause!!).isExactlyInstanceOf(IOException::class.java)
    }
}
