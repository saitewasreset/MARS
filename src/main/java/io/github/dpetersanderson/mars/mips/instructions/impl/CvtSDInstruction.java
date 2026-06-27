package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class CvtSDInstruction extends BasicInstruction {
    public CvtSDInstruction() {
        super(
                "cvt.s.d $f1,$f2",
                "Convert from double precision to single precision : Set $f1 to single precision equivalent of double"
                        + " precision value in $f2",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 00000 sssss fffff 100000",
                statement -> {
                    int[] operands = statement.getOperands();
                    // convert double precision in $f2 to single stored in $f1
                    if (operands[1] % 2 == 1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                    }
                    double val = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    Coprocessor1.updateRegister(operands[0], Float.floatToIntBits((float) val));
                });
    }
}
