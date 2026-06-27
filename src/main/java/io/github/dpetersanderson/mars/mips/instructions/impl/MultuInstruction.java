package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MultuInstruction extends BasicInstruction {
    public MultuInstruction() {
        super(
                "multu $t1,$t2",
                "Multiplication unsigned : Set HI to high-order 32 bits, LO to low-order 32 bits of the product of"
                        + " unsigned $t1 and $t2 (use mfhi to access HI, mflo to access LO)",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 011001",
                statement -> {
                    int[] operands = statement.getOperands();
                    long product = (((long) RegisterFile.getValue(operands[0])) << 32 >>> 32)
                            * (((long) RegisterFile.getValue(operands[1])) << 32 >>> 32);
                    // Register 33 is HIGH and 34 is LOW
                    RegisterFile.updateRegister(33, (int) (product >> 32));
                    RegisterFile.updateRegister(34, (int) ((product << 32) >> 32));
                });
    }
}
