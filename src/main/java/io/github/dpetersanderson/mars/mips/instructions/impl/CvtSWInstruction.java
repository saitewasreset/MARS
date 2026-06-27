package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class CvtSWInstruction extends BasicInstruction {
    public CvtSWInstruction() {
        super(
                "cvt.s.w $f0,$f1",
                "Convert from word to single precision : Set $f0 to single precision equivalent of 32-bit integer"
                        + " value in $f2",
                BasicInstructionFormat.R_FORMAT,
                "010001 10100 00000 sssss fffff 100000",
                statement -> {
                    int[] operands = statement.getOperands();
                    // convert integer to single (interpret $f1 value as int?)
                    Coprocessor1.updateRegister(
                            operands[0], Float.floatToIntBits((float) Coprocessor1.getValue(operands[1])));
                });
    }
}
