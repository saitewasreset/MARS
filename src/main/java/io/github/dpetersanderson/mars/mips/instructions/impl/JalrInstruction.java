package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class JalrInstruction extends BranchingInstruction {
    public JalrInstruction() {
        super(
                "jalr $t1,$t2",
                "Jump and link register : Set $t1 to Program Counter (return address) then jump to statement whose"
                        + " address is in $t2",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 00000 fffff 00000 001001",
                statement -> {
                    int[] operands = statement.getOperands();
                    processReturnAddress(
                            operands[0]); // RegisterFile.updateRegister(operands[0], RegisterFile.getProgramCounter());
                    processJump(RegisterFile.getValue(operands[1]));
                });
    }
}
