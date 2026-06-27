package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class SwlInstruction extends BasicInstruction {
    public SwlInstruction() {
        super(
                "swl $t1,-100($t2)",
                "Store word left : Store high-order 1 to 4 bytes of $t1 into memory, starting with effective byte"
                        + " address and continuing through the low-order byte of its word",
                BasicInstructionFormat.I_FORMAT,
                "101010 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        int address = RegisterFile.getValue(operands[2]) + operands[1];
                        int source = RegisterFile.getValue(operands[0]);
                        for (int i = 0; i <= address % Globals.memory.WORD_LENGTH_BYTES; i++) {
                            Globals.memory.setByte(address - i, Binary.getByte(source, 3 - i));
                        }
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
