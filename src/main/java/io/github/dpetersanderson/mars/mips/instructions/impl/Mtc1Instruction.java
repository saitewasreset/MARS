package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Mtc1Instruction extends BasicInstruction {
    public Mtc1Instruction() {
        super(
                "mtc1 $t1,$f1",
                "Move to Coprocessor 1 (FPU) : Set Coprocessor 1 register $f1 to value in $t1",
                BasicInstructionFormat.R_FORMAT,
                "010001 00100 fffff sssss 00000 000000",
                statement -> {
                    int[] operands = statement.getOperands();
                    Coprocessor1.updateRegister(operands[1], RegisterFile.getValue(operands[0]));
                });
    }
}
