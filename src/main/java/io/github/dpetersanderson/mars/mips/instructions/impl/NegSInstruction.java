package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class NegSInstruction extends BasicInstruction {
    public NegSInstruction() {
        super(
                "neg.s $f0,$f1",
                "Floating point negate single precision : Set single precision $f0 to negation of single precision"
                        + " value in $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 000111",
                statement -> {
                    int[] operands = statement.getOperands();
                    int value = Coprocessor1.getValue(operands[1]);
                    // flip the sign bit
                    Coprocessor1.updateRegister(
                            operands[0], ((value < 0) ? (value & Integer.MAX_VALUE) : (value | Integer.MIN_VALUE)));
                });
    }
}
