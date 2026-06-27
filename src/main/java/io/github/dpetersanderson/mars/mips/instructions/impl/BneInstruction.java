package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class BneInstruction extends BranchingInstruction {
    public BneInstruction() {
        super(
                "bne $t1,$t2,label",
                "Branch if not equal : Branch to statement at label's address if $t1 and $t2 are not equal",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000101 fffff sssss tttttttttttttttt",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) != RegisterFile.getValue(operands[1])) {
                        processBranch(operands[2]);
                    }
                });
    }
}
