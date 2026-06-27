package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class LbInstruction extends BasicInstruction {
    public LbInstruction() {
        super(
                "lb $t1,-100($t2)",
                "Load byte : Set $t1 to sign-extended 8-bit value from effective memory byte address",
                BasicInstructionFormat.I_FORMAT,
                "100000 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        RegisterFile.updateRegister(
                                operands[0],
                                Globals.memory.getByte(RegisterFile.getValue(operands[2]) + (operands[1] << 16 >> 16))
                                        << 24
                                        >> 24);
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
