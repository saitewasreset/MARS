package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class SwrInstruction extends BasicInstruction {
    public SwrInstruction() {
        super(
                "swr $t1,-100($t2)",
                "Store word right : Store low-order 1 to 4 bytes of $t1 into memory, starting with high-order byte of"
                        + " word containing effective byte address and continuing through that byte address",
                BasicInstructionFormat.I_FORMAT,
                "101110 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        int address = RegisterFile.getValue(operands[2]) + operands[1];
                        int source = RegisterFile.getValue(operands[0]);
                        for (int i = 0; i <= 3 - (address % Globals.memory.WORD_LENGTH_BYTES); i++) {
                            Globals.memory.setByte(address + i, Binary.getByte(source, i));
                        }
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
