package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class ShInstruction extends BasicInstruction {
    public ShInstruction() {
        super(
                "sh $t1,-100($t2)",
                "Store halfword : Store the low-order 16 bits of $t1 into the effective memory halfword address",
                BasicInstructionFormat.I_FORMAT,
                "101001 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        Globals.memory.setHalf(
                                RegisterFile.getValue(operands[2]) + (operands[1] << 16 >> 16),
                                RegisterFile.getValue(operands[0]) & 0x0000ffff);
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
