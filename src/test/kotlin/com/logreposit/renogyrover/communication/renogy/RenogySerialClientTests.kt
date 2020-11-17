package com.logreposit.renogyrover.communication.renogy

import com.logreposit.renogyrover.configuration.RenogyConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.IOException

class RenogySerialClientTests {
    @Test
    fun `test connect to slave without specifying a valid rs232 serial adapter connected to a solar charge controller`() {
        val serialClient = RenogySerialClient(RenogyConfiguration())
        val e = assertThrows<RenogySerialClientException> {
            serialClient.readRamRegisters()
        }

        assertThat(e).hasMessage("Caught Exception while establishing connection to slave using Modbus RTU");
        assertThat(e).hasCauseExactlyInstanceOf(IOException::class.java)
        assertThat(e.cause!!.message).containsPattern("^Port \\[/dev/ttyUSB0\\] cannot be opened after \\[3\\] attempts - valid ports are: \\[.*\\]$")
    }
}
