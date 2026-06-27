package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class MovtS2Instruction extends BasicInstruction {
    public MovtS2Instruction() {
        super(
                "movt.s $f0,$f1,1",
                "Move floating point single precision : If condition flag specified by immediate is true, set single"
                        + " precision $f0 to single precision value in $f1e",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 ttt 01 sssss fffff 010001",
                statement -> {
                    int[] operands = statement.getOperands();
                    if (Coprocessor1.getConditionFlag(operands[2]) == 1)
                        Coprocessor1.updateRegister(operands[0], Coprocessor1.getValue(operands[1]));
                });
    }
}
