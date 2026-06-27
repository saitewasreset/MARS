package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MulInstruction extends BasicInstruction {
    public MulInstruction() {
        super(
                "mul $t1,$t2,$t3",
                "Multiplication without overflow  : Set HI to high-order 32 bits, LO and $t1 to low-order 32 bits of"
                        + " the product of $t2 and $t3 (use mfhi to access HI, mflo to access LO)",
                BasicInstructionFormat.R_FORMAT,
                "011100 sssss ttttt fffff 00000 000010",
                statement -> {
                    int[] operands = statement.getOperands();
                    long product =
                            (long) RegisterFile.getValue(operands[1]) * (long) RegisterFile.getValue(operands[2]);
                    RegisterFile.updateRegister(operands[0], (int) ((product << 32) >> 32));
                    // Register 33 is HIGH and 34 is LOW.  Not required by MIPS; SPIM does it.
                    RegisterFile.updateRegister(33, (int) (product >> 32));
                    RegisterFile.updateRegister(34, (int) ((product << 32) >> 32));
                });
    }
}
