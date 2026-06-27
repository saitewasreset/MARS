package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MovtInstruction extends BasicInstruction {
    public MovtInstruction() {
        super(
                "movt $t1,$t2",
                "Move if FP condition flag 0 true : Set $t1 to $t2 if FPU (Coprocessor 1) condition flag 0 is true"
                        + " (one)",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss 000 01 fffff 00000 000001",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (Coprocessor1.getConditionFlag(0) == 1)
                        RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]));
                });
    }
}
