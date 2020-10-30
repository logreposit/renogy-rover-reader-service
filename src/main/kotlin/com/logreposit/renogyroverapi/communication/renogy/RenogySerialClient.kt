package com.logreposit.renogyroverapi.communication.renogy

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster
import com.ghgande.j2mod.modbus.procimg.Register
import com.ghgande.j2mod.modbus.util.SerialParameters
import com.logreposit.renogyroverapi.configuration.RenogyConfiguration
import com.logreposit.renogyroverapi.utils.logger
import org.springframework.stereotype.Service

class RenogySerialClientException(message: String, cause: Throwable? = null) : Exception(message, cause)

@Service
class RenogySerialClient(renogyConfiguration: RenogyConfiguration) {
    val logger = logger()

    val master = ModbusSerialMaster(
            SerialParameters(
                    renogyConfiguration.comPort,
                    9600,
                    0,
                    0,
                    8,
                    1,
                    0,
                    false))

    fun readRamRegisters(): Array<out Register> {
        connectToSlave()

        val registers = readRamRegistersFromModbus()

        if (registers.isNullOrEmpty() || registers.size != 35) {
            throw RenogySerialClientException("The Solar Charge Controller did not return the expected 35 registers")
        }

        logger.info("Received {} registers (from starting address 0x100) from solar charge controller: {}", registers.size, registers)

        return registers
    }

    private fun readRamRegistersFromModbus(): Array<out Register> {
        try {
            return master.readMultipleRegisters(0x100, 35)
        } catch (e: Throwable) {
            logger.error("Caught Exception while reading RAM registers using Modbus RTU", e)

            throw RenogySerialClientException("Caught Exception while reading RAM registers using Modbus RTU", e)
        }
    }

    private fun connectToSlave() {
        try {
            master.connect()
        } catch (e: Throwable) {
            logger.error("Caught Exception while establishing connection to slave using Modbus RTU", e)

            throw RenogySerialClientException("Caught Exception while establishing connection to slave using Modbus RTU", e)
        }
    }
}
