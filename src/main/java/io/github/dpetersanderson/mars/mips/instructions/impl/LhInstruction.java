package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class LhInstruction extends BasicInstruction {
    public LhInstruction() {
        super(
                "lh $t1,-100($t2)",
                "Load halfword : Set $t1 to sign-extended 16-bit value from effective memory halfword address",
                BasicInstructionFormat.I_FORMAT,
                "100001 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        RegisterFile.updateRegister(
                                operands[0],
                                Globals.memory.getHalf(RegisterFile.getValue(operands[2]) + (operands[1] << 16 >> 16))
                                        << 16
                                        >> 16);
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
