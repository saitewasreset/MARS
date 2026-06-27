package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MultInstruction extends BasicInstruction {
    public MultInstruction() {
        super(
                "mult $t1,$t2",
                "Multiplication : Set hi to high-order 32 bits, lo to low-order 32 bits of the product of $t1 and $t2"
                        + " (use mfhi to access hi, mflo to access lo)",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 011000",
                statement -> {
                    int[] operands = statement.getOperands();
                    long product =
                            (long) RegisterFile.getValue(operands[0]) * (long) RegisterFile.getValue(operands[1]);
                    // Register 33 is HIGH and 34 is LOW
                    RegisterFile.updateRegister(33, (int) (product >> 32));
                    RegisterFile.updateRegister(34, (int) ((product << 32) >> 32));
                });
    }
}
