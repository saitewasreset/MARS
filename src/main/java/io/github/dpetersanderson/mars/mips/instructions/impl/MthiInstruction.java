package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MthiInstruction extends BasicInstruction {
    public MthiInstruction() {
        super(
                "mthi $t1",
                "Move to HI registerr : Set HI to contents of $t1 (see multiply and divide operations)",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 010001",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(33, RegisterFile.getValue(operands[0]));
                });
    }
}
