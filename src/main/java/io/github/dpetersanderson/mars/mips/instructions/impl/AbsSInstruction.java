package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class AbsSInstruction extends BasicInstruction {
    public AbsSInstruction() {
        super(
                "abs.s $f0,$f1",
                "Floating point absolute value single precision : Set $f0 to absolute value of $f1, single precision",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 000101",
                statement -> {
                    int[] operands = statement.getOperands();
                    // I need only clear the high order bit!
                    Coprocessor1.updateRegister(operands[0], Coprocessor1.getValue(operands[1]) & Integer.MAX_VALUE);
                });
    }
}
