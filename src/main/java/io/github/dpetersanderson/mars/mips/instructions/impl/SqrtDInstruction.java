package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class SqrtDInstruction extends BasicInstruction {
    public SqrtDInstruction() {
        super(
                "sqrt.d $f2,$f4",
                "Square root double precision : Set $f2 to double-precision floating point square root of $f4",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 00000 sssss fffff 000100",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1 || operands[1] % 2 == 1 || operands[2] % 2 == 1) {
                        throw new ProcessingException(statement, "both registers must be even-numbered");
                    }
                    double value = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    long longSqrt = 0;
                    if (value < 0.0) {
                        // This is subject to refinement later.  Release 4.0 defines floor, ceil, trunc, round
                        // to act silently rather than raise Invalid Operation exception, so sqrt should do the
                        // same.  An intermediate step would be to define a setting for FCSR Invalid Operation
                        // flag, but the best solution is to simulate the FCSR register itself.
                        // FCSR = Floating point unit Control and Status Register.  DPS 10-Aug-2010
                        longSqrt = Double.doubleToLongBits(Double.NaN);
                        // throw new ProcessingException(statement, "Invalid Operation: sqrt of negative number");
                    } else {
                        longSqrt = Double.doubleToLongBits(Math.sqrt(value));
                    }
                    Coprocessor1.updateRegister(operands[0] + 1, Binary.highOrderLongToInt(longSqrt));
                    Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longSqrt));
                });
    }
}
