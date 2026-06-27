package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Bc1t2Instruction extends BranchingInstruction {
    public Bc1t2Instruction() {
        super(
                "bc1t 1,label",
                "Branch if specified FP condition flag true (BC1T, not BCLT) : If Coprocessor 1 condition flag"
                        + " specified by immediate is true (one) then branch to statement at label's address",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "010001 01000 fff 01 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (Coprocessor1.getConditionFlag(operands[0]) == 1) {
                        processBranch(operands[1]);
                    }
                });
    }
}
