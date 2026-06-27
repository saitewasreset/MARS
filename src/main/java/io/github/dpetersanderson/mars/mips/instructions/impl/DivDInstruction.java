package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class DivDInstruction extends BasicInstruction {
    public DivDInstruction() {
        super(
                "div.d $f2,$f4,$f6",
                "Floating point division double precision : Set $f2 to double-precision floating point value of $f4"
                        + " divided by $f6",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 ttttt sssss fffff 000011",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1 || operands[1] % 2 == 1 || operands[2] % 2 == 1) {
                        throw new ProcessingException(statement, "all registers must be even-numbered");
                    }
                    double div1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    double div2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[2] + 1), Coprocessor1.getValue(operands[2])));
                    double quot = div1 / div2;
                    long longQuot = Double.doubleToLongBits(quot);
                    Coprocessor1.updateRegister(operands[0] + 1, Binary.highOrderLongToInt(longQuot));
                    Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longQuot));
                });
    }
}
