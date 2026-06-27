package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Bc1f2Instruction extends BranchingInstruction {
    public Bc1f2Instruction() {
        super(
                "bc1f 1,label",
                "Branch if specified FP condition flag false (BC1F, not BCLF) : If Coprocessor 1 condition flag"
                        + " specified by immediate is false (zero) then branch to statement at label's address",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "010001 01000 fff 00 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (Coprocessor1.getConditionFlag(operands[0]) == 0) {
                        processBranch(operands[1]);
                    }
                });
    }
}
