package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class NopInstruction extends BasicInstruction {
    public NopInstruction() {
        super(
                "nop",
                "Null operation : machine code is all zeroes",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 00000 00000 000000",
                statement -> {
                    // Hey I like this so far!
                });
    }
}
