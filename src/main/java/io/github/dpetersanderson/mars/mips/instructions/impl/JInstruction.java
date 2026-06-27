package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class JInstruction extends BranchingInstruction {
    public JInstruction() {
        super(
                "j target",
                "Jump unconditionally : Jump to statement at target address",
                BasicInstructionFormat.J_FORMAT,
                "000010 ffffffffffffffffffffffffff",
                statement -> {
                    int[] operands = statement.getOperands();
                    processJump(((RegisterFile.getProgramCounter() & 0xF0000000) | (operands[0] << 2)));
                });
    }
}
