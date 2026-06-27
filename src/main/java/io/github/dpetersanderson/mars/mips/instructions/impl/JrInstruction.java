package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class JrInstruction extends BranchingInstruction {
    public JrInstruction() {
        super(
                "jr $t1",
                "Jump register unconditionally : Jump to statement whose address is in $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 00000 00000 001000",
                statement -> {
                    int[] operands = statement.getOperands();
                    processJump(RegisterFile.getValue(operands[0]));
                });
    }
}
