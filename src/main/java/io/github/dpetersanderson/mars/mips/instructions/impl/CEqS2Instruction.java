package io.github.dpetersanderson.mars.mips.instructions.impl;

import io.github.dpetersanderson.mars.mips.hardware.Coprocessor1;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstruction;
import io.github.dpetersanderson.mars.mips.instructions.BasicInstructionFormat;

public class CEqS2Instruction extends BasicInstruction {
    public CEqS2Instruction() {
        super(
                "c.eq.s 1,$f0,$f1",
                "Compare equal single precision : If $f0 is equal to $f1, set Coprocessor 1 condition flag specied by"
                        + " immediate to true else set it to false",
                BasicInstructionFormat.R_FORMAT,
                "010001 10000 ttttt sssss fff 00 11 0010",
                statement -> {
                    int[] operands = statement.getOperands();
                    float op1 = Float.intBitsToFloat(Coprocessor1.getValue(operands[1]));
                    float op2 = Float.intBitsToFloat(Coprocessor1.getValue(operands[2]));
                    if (op1 == op2) Coprocessor1.setConditionFlag(operands[0]);
                    else Coprocessor1.clearConditionFlag(operands[0]);
                });
    }
}
