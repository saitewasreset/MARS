package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SqrtSInstruction extends BasicInstruction {
    public SqrtSInstruction() {
        super(
                "sqrt.s $f0,$f1",
                "Square root single precision : Set $f0 to single-precision floating point square root of $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 000100",
                statement -> {
                    int[] operands = statement.getOperands();
                    float value = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    int floatSqrt = 0;
                    if (value < 0.0f) {
                        // This is subject to refinement later.  Release 4.0 defines floor, ceil, trunc, round
                        // to act silently rather than raise Invalid Operation exception, so sqrt should do the
                        // same.  An intermediate step would be to define a setting for FCSR Invalid Operation
                        // flag, but the best solution is to simulate the FCSR register itself.
                        // FCSR = Floating point unit Control and Status Register.  DPS 10-Aug-2010
                        floatSqrt = Float.floatToIntBits(Float.NaN);
                        // throw new ProcessingException(statement, "Invalid Operation: sqrt of negative number");
                    } else {
                        floatSqrt = Float.floatToIntBits((float) Math.sqrt(value));
                    }
                    Coprocessor1.updateRegister(operands[0], floatSqrt);
                });
    }
}
