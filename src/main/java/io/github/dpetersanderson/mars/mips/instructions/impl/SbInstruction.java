package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SbInstruction extends BasicInstruction {
    public SbInstruction() {
        super(
                "sb $t1,-100($t2)",
                "Store byte : Store the low-order 8 bits of $t1 into the effective memory byte address",
                BasicInstructionFormat.I_FORMAT,
                "101000 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        Globals.memory.setByte(
                                RegisterFile.getValue(operands[2]) + (operands[1] << 16 >> 16),
                                RegisterFile.getValue(operands[0]) & 0x000000ff);
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
