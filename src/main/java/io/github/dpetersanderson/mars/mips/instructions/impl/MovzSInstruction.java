package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MovzSInstruction extends BasicInstruction {
    public MovzSInstruction() {
        super(
                "movz.s $f0,$f1,$t3",
                "Move floating point single precision : If $t3 is zero, set single precision $f0 to single precision"
                        + " value in $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 ttttt sssss fffff 010010",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[2]) == 0)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getValue(operands[1]));
                });
    }
}
