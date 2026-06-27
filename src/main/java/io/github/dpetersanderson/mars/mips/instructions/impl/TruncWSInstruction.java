package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class TruncWSInstruction extends BasicInstruction {
    public TruncWSInstruction() {
        super(
                "trunc.w.s $f0,$f1",
                "Truncate single precision to word : Set $f0 to 32-bit integer truncation of single-precision float in"
                        + " $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 001101",
                statement -> {
                    int[] operands = statement.getOperands();
                    float floatValue = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    int truncate = (int) floatValue; // Typecasting will round toward zero, the correct action
                    // DPS 28-July-2010: Since MARS does not simulate the FSCR, I will take the default
                    // action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                    if (Float.isNaN(floatValue)
                            || Float.isInfinite(floatValue)
                            || floatValue < (float) Integer.MIN_VALUE
                            || floatValue > (float) Integer.MAX_VALUE) {
                        truncate = Integer.MAX_VALUE;
                    }
                    Coprocessor1.updateRegister(operands[0], truncate);
                });
    }
}
