package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor0;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Mtc0Instruction extends BasicInstruction {
    public Mtc0Instruction() {
        super(
                "mtc0 $t1,$8",
                "Move to Coprocessor 0 : Set Coprocessor 0 register $8 to value stored in $t1",
                BasicInstructionFormat.R_FORMAT,
                "010000 00100 fffff sssss 00000 000000",
                statement -> {
                    int[] operands = statement.getOperands();
                    Coprocessor0.updateRegister(operands[1], RegisterFile.getValue(operands[0]));
                });
    }
}
