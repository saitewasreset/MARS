package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class DivInstruction extends BasicInstruction {
    public DivInstruction() {
        super(
                "div $t1,$t2",
                "Division with overflow : Divide $t1 by $t2 then set LO to quotient and HI to remainder (use mfhi to"
                        + " access HI, mflo to access LO)",
                BasicInstructionFormat.R_FORMAT,
                "000000 fffff sssss 00000 00000 011010",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (RegisterFile.getValue(operands[1]) == 0) {
                        // Note: no exceptions and undefined results for zero div
                        // COD3 Appendix A says "with overflow" but MIPS 32 instruction set
                        // specification says "no arithmetic exception under any circumstances".
                        return;
                    }

                    // Register 33 is HIGH and 34 is LOW
                    RegisterFile.updateRegister(
                            33, RegisterFile.getValue(operands[0]) % RegisterFile.getValue(operands[1]));
                    RegisterFile.updateRegister(
                            34, RegisterFile.getValue(operands[0]) / RegisterFile.getValue(operands[1]));
                });
    }
}
