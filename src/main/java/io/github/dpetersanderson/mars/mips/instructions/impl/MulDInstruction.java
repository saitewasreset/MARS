package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.ProcessingException;
import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class MulDInstruction extends BasicInstruction {
    public MulDInstruction() {
        super(
                "mul.d $f2,$f4,$f6",
                "Floating point multiplication double precision : Set $f2 to double-precision floating point value of"
                        + " $f4 times $f6",
                BasicInstructionFormat.R_FORMAT,
                "010001 10001 ttttt sssss fffff 000010",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (operands[0] % 2 == 1 || operands[1] % 2 == 1 || operands[2] % 2 == 1) {
                        throw new ProcessingException(statement, "all registers must be even-numbered");
                    }
                    double mul1 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[1] + 1), Coprocessor1.getValue(operands[1])));
                    double mul2 = Double.longBitsToDouble(Binary.twoIntsToLong(
                            Coprocessor1.getValue(operands[2] + 1), Coprocessor1.getValue(operands[2])));
                    double prod = mul1 * mul2;
                    long longProd = Double.doubleToLongBits(prod);
                    Coprocessor1.updateRegister(operands[0] + 1, Binary.highOrderLongToInt(longProd));
                    Coprocessor1.updateRegister(operands[0], Binary.lowOrderLongToInt(longProd));
                });
    }
}
