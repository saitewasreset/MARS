package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class MsubInstruction extends BasicInstruction {
    public MsubInstruction() {
        super(
                "msub $t1,$t2",
                "Multiply subtract : Multiply $t1 by $t2 then decrement HI by high-order 32 bits of product, decrement"
                        + " LO by low-order 32 bits of product (use mfhi to access HI, mflo to access LO)",
                BasicInstructionFormat.R_FORMAT,
                "011100 fffff sssss 00000 00000 000100",
                statement -> {
                    int[] operands = statement.getOperands();
                    long product =
                            (long) RegisterFile.getValue(operands[0]) * (long) RegisterFile.getValue(operands[1]);
                    // Register 33 is HIGH and 34 is LOW.
                    long contentsHiLo = Binary.twoIntsToLong(RegisterFile.getValue(33), RegisterFile.getValue(34));
                    long diff = contentsHiLo - product;
                    RegisterFile.updateRegister(33, Binary.highOrderLongToInt(diff));
                    RegisterFile.updateRegister(34, Binary.lowOrderLongToInt(diff));
                });
    }
}
