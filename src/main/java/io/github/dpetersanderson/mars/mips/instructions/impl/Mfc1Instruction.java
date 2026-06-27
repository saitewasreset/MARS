package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Mfc1Instruction extends BasicInstruction {
    public Mfc1Instruction() {
        super(
                "mfc1 $t1,$f1",
                "Move from Coprocessor 1 (FPU) : Set $t1 to value in Coprocessor 1 register $f1",
                BasicInstructionFormat.R_FORMAT,
                "010001 00000 fffff sssss 00000 000000",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(operands[0], Coprocessor1.getValue(operands[1]));
                });
    }
}
