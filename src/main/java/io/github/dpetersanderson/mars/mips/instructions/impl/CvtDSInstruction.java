package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class CvtDSInstruction extends BasicInstruction {
    public CvtDSInstruction() {
        super(
                "cvt.d.s $f2,$f1",
                "Convert from single precision to double precision : Set $f2 to double precision equivalent of single"
                        + " precision value in $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 00000 sssss fffff 100001",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1) {
                        throw new ProcessingException(statement, "first register must be even-numbered");
                    }
                    // convert single precision in $f1 to double stored in $f2
                    long result =
                            Double.doubleToLongBits((double) Float.intBitsToFloat(Coprocessor1.getValue(operands[1])));
                    Coprocessor1.updateRegister(operands[0] + 1, Binary.highOrderLongToInt(result));
                    Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(result));
                });
    }
}
