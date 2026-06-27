package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MfloInstruction extends BasicInstruction {
    public MfloInstruction() {
        super(
                "mflo $t1",
                "Move from LO register : Set $t1 to contents of LO (see multiply and divide operations)",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 fffff 00000 010010",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(operands[0], RegisterFile.getValue(34));
                });
    }
}
