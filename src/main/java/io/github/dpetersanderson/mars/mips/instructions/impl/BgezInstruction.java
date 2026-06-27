package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class BgezInstruction extends BranchingInstruction {
    public BgezInstruction() {
        super(
                "bgez $t1,label",
                "Branch if greater than or equal to zero : Branch to statement at label's address if $t1 is greater"
                        + " than or equal to zero",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000001 fffff 00001 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) >= 0) {
                        processBranch(operands[1]);
                    }
                });
    }
}
