package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class LwrInstruction extends BasicInstruction {
    public LwrInstruction() {
        super(
                "lwr $t1,-100($t2)",
                "Load word right : Load from 1 to 4 bytes right-justified into $t1, starting with effective memory"
                        + " byte address and continuing through the high-order byte of its word",
                BasicInstructionFormat.I_FORMAT,
                "100110 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        int address = RegisterFile.getValue(operands[2]) + operands[1];
                        int result = RegisterFile.getValue(operands[0]);
                        for (int i = 0; i <= 3 - (address % Globals.memory.WORD_LENGTH_BYTES); i++) {
                            result = Binary.setByte(result, i, Globals.memory.getByte(address + i));
                        }
                        RegisterFile.updateRegister(operands[0], result);
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
