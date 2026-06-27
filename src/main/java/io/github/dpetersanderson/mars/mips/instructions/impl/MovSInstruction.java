package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MovSInstruction extends BasicInstruction {
    public MovSInstruction() {
        super(
                "mov.s $f0,$f1",
                "Move floating point single precision : Set single precision $f0 to single precision value in $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 000110",
                statement -> {
                    int[] operands = statement.getOperands();
                    Coprocessor1.updateRegister(operands[0], Coprocessor1.getValue(operands[1]));
                });
    }
}
