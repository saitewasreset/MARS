package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class Break2Instruction extends BasicInstruction {
    public Break2Instruction() {
        super(
                "break",
                "Break execution : Terminate program execution with exception",
                BasicInstructionFormat.R_FORMAT,
                "000000 00000 00000 00000 00000 001101",
                statement -> {
                    // At this time I don't have exception processing or trap
                    // handlers
                    // so will just halt execution with a message.
                    throw new ProcessingException(
                            statement, "break instruction executed; no code given.", Exceptions.BREAKPOINT_EXCEPTION);
                });
    }
}
