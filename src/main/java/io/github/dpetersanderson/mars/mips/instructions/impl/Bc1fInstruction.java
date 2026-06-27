package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Bc1fInstruction extends BranchingInstruction {
    public Bc1fInstruction() {
        super(
                "bc1f label",
                "Branch if FP condition flag 0 false (BC1F, not BCLF) : If Coprocessor 1 condition flag 0 is false"
                        + " (zero) then branch to statement at label's address",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "010001 01000 00000 ffffffffffffffff",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (Coprocessor1.getConditionFlag(0) == 0) {
                        processBranch(operands[0]);
                    }
                });
    }
}
