package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class JalInstruction extends BranchingInstruction {
    public JalInstruction() {
        super(
                "jal target",
                "Jump and link : Set $ra to Program Counter (return address) then jump to statement at target address",
                BasicInstructionFormat.J_FORMAT,
                "000011 ffffffffffffffffffffffffff",
                statement -> {
                    int[] operands = statement.getOperands();
                    processReturnAddress(31); // RegisterFile.updateRegister(31, RegisterFile.getProgramCounter());
                    processJump((RegisterFile.getProgramCounter() & 0xF0000000) | (operands[0] << 2));
                });
    }
}
