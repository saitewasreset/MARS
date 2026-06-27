package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class BlezInstruction extends BranchingInstruction {
    public BlezInstruction() {
        super(
                "blez $t1,label",
                "Branch if less than or equal to zero : Branch to statement at label's address if $t1 is less than or"
                        + " equal to zero",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000110 fffff 00000 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) <= 0) {
                        processBranch(operands[1]);
                    }
                });
    }
}
