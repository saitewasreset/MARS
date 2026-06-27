package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.Globals;
import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.AddressErrorException;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class LhuInstruction extends BasicInstruction {
    public LhuInstruction() {
        super(
                "lhu $t1,-100($t2)",
                "Load halfword unsigned : Set $t1 to zero-extended 16-bit value from effective memory halfword address",
                BasicInstructionFormat.I_FORMAT,
                "100101 ttttt fffff ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    try {
                        // offset is sign-extended and loaded halfword value is zero-extended
                        RegisterFile.updateRegister(
                                operands[0],
                                Globals.memory.getHalf(RegisterFile.getValue(operands[2]) + (operands[1] << 16 >> 16))
                                        & 0x0000ffff);
                    } catch (AddressErrorException e) {
                        throw new ProcessingException(statement, e);
                    }
                });
    }
}
