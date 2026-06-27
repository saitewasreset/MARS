package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Jalr2Instruction extends BranchingInstruction {
    public Jalr2Instruction() {
        super(
                "jalr $t1",
                "Jump and link register : Set $ra to Program Counter (return address) then jump to statement whose"
                        + " address is in $t1",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff 00000 11111 00000 001001",
                statement -> {
                    int[] operands = statement.getOperands();
                    processReturnAddress(31); // RegisterFile.updateRegister(31, RegisterFile.getProgramCounter());
                    processJump(RegisterFile.getValue(operands[0]));
                });
    }
}
