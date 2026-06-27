package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor0;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Mfc0Instruction extends BasicInstruction {
    public Mfc0Instruction() {
        super(
                "mfc0 $t1,$8",
                "Move from Coprocessor 0 : Set $t1 to the value stored in Coprocessor 0 register $8",
                BasicInstructionFormat.R_FORMAT,
                "010000 00000 fffff sssss 00000 000000",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(operands[0], Coprocessor0.getValue(operands[1]));
                });
    }
}
