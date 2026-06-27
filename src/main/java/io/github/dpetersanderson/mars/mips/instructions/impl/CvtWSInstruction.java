package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class CvtWSInstruction extends BasicInstruction {
    public CvtWSInstruction() {
        super(
                "cvt.w.s $f0,$f1",
                "Convert from single precision to word : Set $f0 to 32-bit integer equivalent of single precision"
                        + " value in $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 100100",
                statement -> {
                    int[] operands = statement.getOperands();
                    // convert single precision in $f1 to integer stored in $f0
                    Coprocessor1.updateRegister(
                            operands[0], (int) Float.intBitsToFloat(Coprocessor1.getValue(operands[1])));
                });
    }
}
