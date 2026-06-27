package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class DivSInstruction extends BasicInstruction {
    public DivSInstruction() {
        super(
                "div.s $f0,$f1,$f3",
                "Floating point division single precision : Set $f0 to single-precision floating point value of $f1"
                        + " divided by $f3",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 ttttt sssss fffff 000011",
                statement -> {
                    int[] operands = statement.getOperands();
                    float div1 = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    float div2 = Float.intBitsToFloat(Coprocessor1.getValue(operands[2]));
                    float quot = div1 / div2;
                    Coprocessor1.updateRegister(operands[0], Float.floatToIntBits(quot));
                });
    }
}
