package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MulSInstruction extends BasicInstruction {
    public MulSInstruction() {
        super(
                "mul.s $f0,$f1,$f3",
                "Floating point multiplication single precision : Set $f0 to single-precision floating point value of"
                        + " $f1 times $f3",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 ttttt sssss fffff 000010",
                statement -> {
                    int[] operands = statement.getOperands();
                    float mul1 = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    float mul2 = Float.intBitsToFloat(Coprocessor1.getValue(operands[2]));
                    float prod = mul1 * mul2;
                    Coprocessor1.updateRegister(operands[0], Float.floatToIntBits(prod));
                });
    }
}
