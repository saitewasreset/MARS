package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class DivuInstruction extends BasicInstruction {
    public DivuInstruction() {
        super(
                "divu $t1,$t2",
                "Division unsigned without overflow : Divide unsigned $t1 by $t2 then set LO to quotient and HI to"
                        + " remainder (use mfhi to access HI, mflo to access LO)",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 011011",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[1]) == 0) {
                        // Note: no exceptions, and undefined results for zero divide
                        return;
                    }
                    long oper1 = ((long) RegisterFile.getValue(operands[0])) << 32 >>> 32;
                    long oper2 = ((long) RegisterFile.getValue(operands[1])) << 32 >>> 32;
                    // Register 33 is HIGH and 34 is LOW
                    RegisterFile.updateRegister(33, (int) (((oper1 % oper2) << 32) >> 32));
                    RegisterFile.updateRegister(34, (int) (((oper1 / oper2) << 32) >> 32));
                });
    }
}
