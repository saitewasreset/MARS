package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class BltzalInstruction extends BranchingInstruction {
    public BltzalInstruction() {
        super(
                "bltzal $t1,label",
                "Branch if less than zero and link : If $t1 is less than or equal to zero, then set $ra to the Program"
                        + " Counter and branch to statement at label's address",
                BasicInstructionFormat.I_BRANCH_FORMAT,
                "000001 fffff 10000 ssssssssssssssss",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[0]) < 0) { // the "and link" part
                        processReturnAddress(
                                31); // RegisterFile.updateRegister("$ra",RegisterFile.getProgramCounter());
                        processBranch(operands[1]);
                    }
                });
    }
}
