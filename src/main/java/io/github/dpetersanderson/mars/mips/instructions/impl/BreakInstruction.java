package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.simulator.Exceptions;

public class BreakInstruction extends BasicInstruction {
    public BreakInstruction() {
        super(
                "break 100",
                "Break execution with code : Terminate program execution with specified exception code",
                BasicInstructionFormat.R_FORMAT,
                "000000 ffffffffffffffffffff 001101",
                statement -> {
                    // At this time I don't have exception processing or trap
                    // handlers
                    // so will just halt execution with a message.
                    int[] operands = statement.getOperands();
                    throw new ProcessingException(
                            statement,
                            "break instruction executed; code = " + operands[0] + ".",
                            Exceptions.BREAKPOINT_EXCEPTION);
                });
    }
}
