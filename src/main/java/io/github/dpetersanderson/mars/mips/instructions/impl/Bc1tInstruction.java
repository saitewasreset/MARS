package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Bc1tInstruction extends BranchingInstruction {
    public Bc1tInstruction() {
        super(
                "bc1t label",
                "Branch if FP condition flag 0 true (BC1T, not BCLT) : If Coprocessor 1 condition flag 0 is true (one)"
                        + " then branch to statement at label's address",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "010001 01000 00001 ffffffffffffffff",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (Coprocessor1.getConditionFlag(0) == 1) {
                        processBranch(operands[0]);
                    }
                });
    }
}
