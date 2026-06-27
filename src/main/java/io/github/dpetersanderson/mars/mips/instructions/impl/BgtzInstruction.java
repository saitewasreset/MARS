package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class BgtzInstruction extends BranchingInstruction {
    public BgtzInstruction() {
        super(
                "bgtz $t1,label",
                "Branch if greater than zero : Branch to statement at label's address if $t1 is greater than zero",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000111 fffff 00000 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) > 0) {
                        processBranch(operands[1]);
                    }
                });
    }
}
