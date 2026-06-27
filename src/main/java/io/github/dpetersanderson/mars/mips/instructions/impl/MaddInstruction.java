package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;
import io.github.dpetersanderson.mars.util.Binary;

public class MaddInstruction extends BasicInstruction {
    public MaddInstruction() {
        super(
                "madd $t1,$t2",
                "Multiply add : Multiply $t1 by $t2 then increment HI by high-order 32 bits of product, increment LO"
                        + " by low-order 32 bits of product (use mfhi to access HI, mflo to access LO)",
                BasicInstructionFormat.R_FORMAT,
                "011100 fffff sssss 00000 00000 000000",
                statement -> {
                    int[] operands = statement.getOperands();
                    long product =
                            (long) RegisterFile.getValue(operands[0]) * (long) RegisterFile.getValue(operands[1]);
                    // Register 33 is HIGH and 34 is LOW.
                    long contentsHiLo = Binary.twoIntsToLong(RegisterFile.getValue(33), RegisterFile.getValue(34));
                    long sum = contentsHiLo + product;
                    RegisterFile.updateRegister(33, Binary.highOrderLongToInt(sum));
                    RegisterFile.updateRegister(34, Binary.lowOrderLongToInt(sum));
                });
    }
}
