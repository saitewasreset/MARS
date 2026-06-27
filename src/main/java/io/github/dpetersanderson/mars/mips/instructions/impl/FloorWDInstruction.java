package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class FloorWDInstruction extends BasicInstruction {
    public FloorWDInstruction() {
        super(
                "floor.w.d $f1,$f2",
                "Floor double precision to word : Set $f1 to 32-bit integer floor of double-precision float in $f2",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 00000 sssss fffff 001111",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[1] % 2 == 1) {
                        throw new ProcessingException(statement, "second register must be even-numbered");
                    }
                    double doubleValue = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    // DPS 27-July-2010: Since MARS does not simulate the FSCR, I will take the default
                    // action of setting the result to 2^31-1, if the value is outside the 32 bit range.
                    int floor = (int) Math.floor(doubleValue);
                    if (Double.isNaN(doubleValue)
                            || Double.isInfinite(doubleValue)
                            || doubleValue < (double) Integer.MIN_VALUE
                            || doubleValue > (double) Integer.MAX_VALUE) {
                        floor = Integer.MAX_VALUE;
                    }
                    Coprocessor1.updateRegister(operands[0], floor);
                });
    }
}
