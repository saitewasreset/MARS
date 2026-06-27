package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class AddSInstruction extends BasicInstruction {
    public AddSInstruction() {
        super(
                "add.s $f0,$f1,$f3",
                "Floating point addition single precision : Set $f0 to single-precision floating point value of $f1"
                        + " plus $f3",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 ttttt sssss fffff 000000",
                statement -> {
                    int[] operands = statement.getOperands();
                    float add1 = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    float add2 = Float.intBitsToFloat(Coprocessor1.getValue(operands[2]));
                    float sum = add1 + add2;
                    // overflow detected when sum is positive or negative infinity.
                    /*
                    if (sum == Float.NEGATIVE_INFINITY || sum == Float.POSITIVE_INFINITY) {
                      throw new ProcessingException(statement,"arithmetic overflow");
                    }
                    */
                    Coprocessor1.updateRegister(operands[0], Float.floatToIntBits(sum));
                });
    }
}
