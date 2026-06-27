package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.hardware.RegisterFile;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class Movt2Instruction extends BasicInstruction {
    public Movt2Instruction() {
        super(
                "movt $t1,$t2,1",
                "Move if specfied FP condition flag true : Set $t1 to $t2 if FPU (Coprocessor 1) condition flag"
                        + " specified by the immediate is true (one)",
                BasicInstructionFormat.R_FORMAT,
                "000000 sssss ttt 01 fffff 00000 000001",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (Coprocessor1.getConditionFlag(operands[2]) == 1)
                        RegisterFile.updateRegister(operands[0], RegisterFile.getValue(operands[1]));
                });
    }
}
