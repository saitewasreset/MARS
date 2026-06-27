package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class SltInstruction extends BasicInstruction {
    public SltInstruction() {
        super(
                "slt $t1,$t2,$t3",
                "Set less than : If $t2 is less than $t3, then set $t1 to 1 else set $t1 to 0",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttttt fffff 00000 101010",
                statement -> {
                    int[] operands = statement.getOperands();
                    RegisterFile.updateRegister(
                            operands[0],
                            (RegisterFile.getValue(operands[1]) < RegisterFile.getValue(operands[2])) ? 1 : 0);
                });
    }
}
