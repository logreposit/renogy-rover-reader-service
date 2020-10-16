package com.logreposit.renogyroverapi.communication.serial

import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster
import com.ghgande.j2mod.modbus.procimg.Register
import com.ghgande.j2mod.modbus.util.SerialParameters
import com.logreposit.renogyroverapi.configuration.RenogyConfiguration
import com.logreposit.renogyroverapi.utils.logger
import org.springframework.stereotype.Service

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
        master.connect()

        val registers = master.readMultipleRegisters(0x100, 35)

        if (registers.isNullOrEmpty()) {
            logger.error("master returned null when reading registers!")

            throw NullPointerException("master returned null when reading registers")
        }

        logger.info("Received {} registers (from starting address 0x100) from solar charge controller: {}", registers.size, registers)

        return registers
    }
}
